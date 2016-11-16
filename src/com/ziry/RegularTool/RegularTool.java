package com.ziry.RegularTool;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;

import org.jvnet.substance.skin.SubstanceAutumnLookAndFeel;

/**
 * 这是一个用java开发的正则表达式调试工具
 * @author Ziry
 * 2016-07-14 ： 提交到各开源网站交流学习
 */
public class RegularTool extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	//版本号
	public static final String VERSION = "V0.3";
	
	//设置用户界面居中
	public static final int WIDTH = 800;
	public static final int HEIGHT = 700;
	private Toolkit tk = Toolkit.getDefaultToolkit();   
	private Dimension d = tk.getScreenSize();   		
	private int y = d.height/2-HEIGHT/2;  			//居中坐标X
	private int x = d.width/2-WIDTH/2;				//居中坐标y
	
	//正则表达式
	JTextField regulat_JTF = new JTextField(40);
	JButton regulat_JB = new JButton("取匹配文本");
	JButton reset_JB = new JButton("重置");
	JPanel regular_JP = new JPanel();
	
	//被搜索文本
	JTextArea seek_JTA = new JTextArea();
	JScrollPane seek_JSP = new JScrollPane(seek_JTA);
	
	//搜索结果
	String[]columnNames = {"序列","匹配文本","子匹配文本"};
	Object[][] rowData = {};
	DefaultTableModel defaultTableModel = new DefaultTableModel(rowData, columnNames);
	JTable reult_JT = new JTable(defaultTableModel);
	JScrollPane reult_JSP = new JScrollPane(reult_JT);	
	
	JPanel center_JP = new JPanel(new GridLayout(1,2));
	
	//east边面板
	String[] columnNames_examples = {"示例","表达式"};
	Object[][] rowData_examples = {};
	DefaultTableModel defaultTableModel_examples = new DefaultTableModel(rowData_examples, columnNames_examples);
	JTable examples_JT = new JTable(defaultTableModel_examples);
	JScrollPane examples_JSP = new JScrollPane(examples_JT);
	
	//得到图标
	Image imageIco = Toolkit.getDefaultToolkit().getImage("images/logo.jpg");			
	
	public RegularTool() {
		
		resetExamples();															//加载示例JTable
		
		regular_JP.add(regulat_JTF);
		regular_JP.add(regulat_JB);
		regular_JP.add(reset_JB);
		regular_JP.setBorder(BorderFactory.createTitledBorder("正则表达式"));			//设置边框
		this.add(regular_JP, BorderLayout.NORTH);
		
		center_JP.add(seek_JSP);
		center_JP.add(examples_JSP);
		seek_JSP.setBorder(BorderFactory.createTitledBorder("被搜索文本"));			//设置边框
		this.add(center_JP, BorderLayout.CENTER);

		reult_JSP.setPreferredSize(new Dimension(800, 300));
		reult_JSP.setBorder(BorderFactory.createTitledBorder("搜索结果"));			//设置边框
		this.add(reult_JSP, BorderLayout.SOUTH);
		
		regulat_JB.addActionListener(this);											//增加按钮监听器
		reset_JB.addActionListener(this);											//增加按钮监听器
		
		this.setTitle("Ziry作品:正则表达式调试工具"+VERSION);
		this.setIconImage(imageIco);												//设置图标
		this.setBounds(x, y, WIDTH, HEIGHT);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
	public static void main(String[] args) {
		
		//使标题栏的风格也跟着一起改变
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		//设置皮肤
		try {
			UIManager.setLookAndFeel(  new SubstanceAutumnLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		new RegularTool();
	}

	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == regulat_JB) {
			
			resetReult();			//重置结果集
			
			String rex = regulat_JTF.getText();					//得到输入的表达式
			String seek = seek_JTA.getText();					//得到被搜索文本
			
			//如果未输入弹出提示
			if(rex.length()==0) {
				JOptionPane.showMessageDialog(null, "请输入表达式文本！",
						"提示",JOptionPane.INFORMATION_MESSAGE);
			} else if(seek.length()==0) {
				JOptionPane.showMessageDialog(null, "请输入被搜索文本！",
						"提示",JOptionPane.INFORMATION_MESSAGE);
			} else {
				if(dispose(rex, seek)){
					JOptionPane.showMessageDialog(null, "匹配数为零！",
							"提示",JOptionPane.INFORMATION_MESSAGE);
				}
			}
			
		}
		if(e.getSource() == reset_JB) {
			regulat_JTF.setText("");
			seek_JTA.setText("");
			resetReult();			//重置结果集
			resetExamples();		//重置示例
		}
	}
	
	//处理
	public boolean dispose(String rex, String seek) {
		//判断匹配数是否为零，默认为零
		boolean isNull = true;
		//编译正则表达式
		Pattern p = null;
		try {
			p = Pattern.compile(rex);
		} catch(PatternSyntaxException e) {
			JOptionPane.showMessageDialog(null, "正则表达式错误！",
					"提示",JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		//匹配正则表达式
		Matcher m = p.matcher(seek);
		//序号
		int id = 1;
		while( m.find() ) {
			String str0 = m.group();			
			String str1 = "";
			try {
				str1 = m.group(1);								//捕获的子序列
			}catch(Exception e) {
			}
				Object[] o = {id+"",str0,str1};
			defaultTableModel.addRow(o);						//添加到JTable
			id++;
			isNull = false;
		}
		return isNull;
	}
	
	//重置结果集
	public void resetReult(){
		int rowCount = defaultTableModel.getRowCount();
		for(int i=0; i<rowCount; i++) {
			defaultTableModel.removeRow(0);
		}
	}
	
	//刷新示例集
	public void resetExamples() {
		//先清空
		int rowCount = defaultTableModel_examples.getRowCount();
		for(int i=0; i<rowCount; i++) {
			defaultTableModel_examples.removeRow(0);
		}
		//再添加
		try {
			FileInputStream fis = new FileInputStream("data/data.properties");
			Properties pro = new Properties();
			pro.load(fis);
			Enumeration<?> e =  pro.propertyNames();
			while(e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String valu = pro.getProperty(key);
				Object[] oo = {new String(key.getBytes("ISO-8859-1"),"UTF-8"),
						new String(valu.getBytes("ISO-8859-1"),"UTF-8")};
				defaultTableModel_examples.addRow(oo);						//添加到JTable
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
