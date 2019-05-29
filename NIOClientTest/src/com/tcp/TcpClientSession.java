package com.tcp;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

import com.common.StringUtil;
import com.ui.swing.SocketClientWin;

public class TcpClientSession {

	private SocketClientWin pWin;
	
	private Selector selector;
	private SocketChannel socketChannel;
//	private ExecutorService executor;
	private boolean selRunning;
	
	private ReentrantLock eventLock ;
	private boolean readingData;
	private boolean registered;
	
	protected String ip;
	protected String port;
	
	
	private volatile Thread ownerThread = Thread.currentThread();
	
	public TcpClientSession(SocketClientWin pWin){
		this.pWin = pWin;
		try{
			init();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public void init() throws Exception{	
		
		eventLock = new ReentrantLock();
		this.registered = this.readingData = false;
		
		selRunning = true;		
		selector = Selector.open();	
		
		
//		executor = Executors.newFixedThreadPool(1);
//		executor.execute(new Runnable(){
//			public void run(){
//				try{
//					readSelect();
//				}catch(Exception e){
//					e.printStackTrace();
//				}
//			}
//		});
		
		( new Thread( new Runnable(){
			public void run(){
				try{
					readSelect();
				}catch(Exception e){
					log(e.toString());
					e.printStackTrace();
				}
			}
		})).start();
	}
	
	protected final void startReading(){
		this.eventLock.lock();
		try{
			this.readingData = true;
		}finally{
			this.eventLock.unlock();
		}
		
//		registerRead();
	}
	
//	protected registerRead(){
//		this.nioSelector.registerLister(this,this.channel, SelectionKey.OP_READ);
//	}
	
	public void readSelect() throws Exception{
		log("Selector Reading.... seleRuunning :" + selRunning);
		
		while(selRunning){			
			int selCnt = selector.select(10L);			
			if( selCnt > 0){
				log("TcpClinetSession select Event 발생 cnt :"+ selCnt);
				Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
				while(keyIter.hasNext()){
					SelectionKey selKey = keyIter.next();
					if( selKey.isReadable()){
						log(" 클라이언트 read 설렉션 이벤트 발생.");
						readData();
					}else if(selKey.isWritable()){
						log(" 클라이언트 write 설렉션 이벤트 발생.");
						selKey.interestOps(SelectionKey.OP_READ);
					}else if(selKey.isConnectable()){
						log(" 클라이언트 connect 설렉션 이벤트 발생.");
						SocketChannel channel = (SocketChannel)selKey.channel();
						if( channel.finishConnect()){
							log("클라이언트 연결 성공...");
							selKey.interestOps(SelectionKey.OP_WRITE);
						}											
					}
					
					keyIter.remove();
				}
//				Iterator iter = selector.selectedKeys().iterator();
//				selector.
			}else{
//				log("클라이언트 selector 아직 이벤트 없다. selCnt :"+ selCnt);
			}
		}
	}
	
	public void readData() throws Exception{

		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024*1024);
		int readCnt = socketChannel.read(byteBuffer);
		
		byteBuffer.flip();
		int limit = byteBuffer.limit();
		byte[] data = new byte[limit];		
		byteBuffer.get(data);
		
		String str = String.format("데이터 수신 ReadCnt[%d], limit[%d], readData[%s]",readCnt, limit, new String(data));
		log(str);
	}
	
	public void connect(String ip, String port) throws Exception{	
		if( socketChannel != null && socketChannel.isOpen()){
			socketChannel.close();
		}
		this.ip = ip;
		this.port = port;
		
		socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);	
		socketChannel.register(selector, SelectionKey.OP_CONNECT);
		
		InetSocketAddress serverAddr = new InetSocketAddress(ip, Integer.parseInt(port));
		socketChannel.connect(serverAddr);
		
		selector.wakeup();
		
//		socketChannel.register(selector, SelectionKey.OP_CONNECT);		
//		Socket socket = socketChannel.socket();
//		socket.setKeepAlive(true);
//		socket.setTcpNoDelay(true);
		
		//SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_READ, null);
		log("소켓연결 .... ip:"+ ip+", port:"+ port);
//		selector.wakeup();
	}
	
	
	public void unregisterRead(SelectionKey paramSelectionKey){
		if(!(this.eventLock.tryLock())){
			return;
		}
		try{
			if((!(this.readingData)) && hasReadingThread()){
				this.registered = false;
				paramSelectionKey.interestOps( paramSelectionKey.interestOps() & 0xFFFFFFFE);
			}
		}finally{
			this.eventLock.unlock();
			log("이제 돌아왔군요. 천천히 쓰면 되는 것인가...");
		}
	}
	
	protected boolean hasReadingThread(){
		return (null == this.ownerThread);
	}
	
	
	public void disConnect() throws Exception{
		
		// 설렉터 쓰레드 중지시키고
		selRunning = false;
		// 썰렉터를 기동시키고 있는 쓰레드 풀을 중지 시키고..
//		executor.shutdown();
		
		// 클라이언트 소켓을 닫고.. NIO Client channel 도 닫고..
		if( socketChannel != null && socketChannel.isOpen()){
			socketChannel.socket().close();
			socketChannel.close();
		}		
		
		log("연결끊김...");
	}
	
	public void writeData(String msg, String encType) throws Exception{
//		byte[] buffer = new byte[1024*1024];		
		Charset charSet = Charset.forName(encType);
		msg = appendHeaderLength(msg, encType);
		ByteBuffer buffer =  charSet.encode(msg);	
		pWin.log(msg);
		socketChannel.write(buffer);
		
		
		(socketChannel.keyFor(selector)).interestOps(SelectionKey.OP_READ);	
		selector.wakeup();		
//		log("client socket data write data["+ charSet.decode(buffer).toString() +"]");			
		
//		readData();
	}
	
	/**
	 * 패킷의 8byte 헤더길이 스트링 추가 해서 msg 생성
	 * @param msg
	 * @return
	 */
	protected String appendHeaderLength(String msg, String encType) throws Exception{
		StringBuffer buffer = new StringBuffer();
		int bodyLength = msg.getBytes(encType).length;
		String headerLength = StringUtil.lpadStr(bodyLength + 8, 8, '0');
		buffer.append(headerLength).append(msg);
		
		return buffer.toString();
	}
	
	
	

	public void log(String msg){
		System.out.println(msg+"\n");
		pWin.log(msg);
	}
}
