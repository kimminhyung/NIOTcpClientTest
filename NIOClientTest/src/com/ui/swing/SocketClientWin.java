package com.ui.swing;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

import com.tcp.TcpClientSession;
import javax.swing.JLabel;
import java.awt.Color;
import javax.swing.border.MatteBorder;

import org.eclipse.swt.widgets.MessageBox;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SocketClientWin {

	private JFrame frame;
	private JTextField txtSend;
	
	private TcpClientSession tcpSession ;
	private JTextField txtIp;
	private JTextField txtPort;
	JTextArea txtLog;
	
	private final String encType = "euc-kr";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SocketClientWin window = new SocketClientWin();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SocketClientWin() {
		initialize();
		init();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.WHITE);
		frame.getContentPane().setLayout(null);
		
		txtSend = new JTextField();
		txtSend.setText("안녕하세요");
		txtSend.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if( arg0.getKeyCode() == KeyEvent.VK_ENTER){
					send();
				}
			}			
		});
		txtSend.setBounds(14, 334, 510, 27);
		frame.getContentPane().add(txtSend);
		txtSend.setColumns(10);
		txtSend.setFocusable(true);
		
		JButton btnSend = new JButton("전송");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send();
			}
		});
		btnSend.setBounds(526, 334, 86, 27);
		frame.getContentPane().add(btnSend);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(14, 24, 500, 300);
		frame.getContentPane().add(scrollPane);
		
		txtLog = new JTextArea();
		txtLog.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		scrollPane.setViewportView(txtLog);
		txtLog.setBackground(Color.WHITE);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setBounds(602, 24, 116, 24);
		frame.getContentPane().add(comboBox);
		
		JButton btnConnect = new JButton("연결");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				connect();
			}
		});
		btnConnect.setBounds(602, 132, 105, 27);
		frame.getContentPane().add(btnConnect);
		frame.setTitle("클라이언트 윈도우");
		
		txtIp = new JTextField();
		txtIp.setText("127.0.0.1");
		txtIp.setBounds(602, 60, 116, 24);
		frame.getContentPane().add(txtIp);
		txtIp.setColumns(10);
		
		txtPort = new JTextField();
		txtPort.setText("7001");
		txtPort.setBounds(602, 96, 116, 24);
		frame.getContentPane().add(txtPort);
		txtPort.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Protocol");
		lblNewLabel.setBounds(526, 27, 62, 18);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblIp = new JLabel("IP");
		lblIp.setBounds(528, 63, 62, 18);
		frame.getContentPane().add(lblIp);
		
		JLabel lblPort = new JLabel("Port");
		lblPort.setBounds(528, 98, 62, 21);
		frame.getContentPane().add(lblPort);
		
		JButton btnStop = new JButton("연결끊기");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopConnect();
			}
		});
		btnStop.setBounds(602, 168, 105, 27);
		frame.getContentPane().add(btnStop);
		
		JButton btnClose = new JButton("닫기");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopConnect();
				System.exit(0);
			}
		});
		btnClose.setBounds(626, 334, 90, 27);
		frame.getContentPane().add(btnClose);
		
		JButton btnClear = new JButton("클리어");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				txtLog.setText("");
			}
		});
		btnClear.setBounds(602, 205, 105, 23);
		frame.getContentPane().add(btnClear);
		frame.setBounds(90, 100, 751, 432);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void init(){
		try{
			tcpSession = new TcpClientSession(this);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	// 서버연결
	public void connect(){
		try{
			String ip = txtIp.getText();
			String port = txtPort.getText();
			tcpSession.connect(ip, port);
		}catch(Exception e){
			e.printStackTrace();
			log("연결실패 : "+ e.toString());
		}
	}
	
	// 서버랑 연결끊기
	public void stopConnect(){
		try{
			tcpSession.disConnect();
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	
	// 데이터 전송
	public void send(){
		try{
			String msg = txtSend.getText();					
			tcpSession.writeData(msg, encType);
			log("메세지송신 :" + msg);	
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void log(String msg){
		txtLog.append(msg+"\n");
		txtLog.setCaretPosition(txtLog.getDocument().getLength());
	}
}






