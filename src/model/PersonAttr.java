package model;

/**
 * Created by Superxlcr
 * �û�������
 */
public class PersonAttr {
	private String name; // ��������
	private int number; // ����ֵ
	private String color; // ����������ɫ
	private String shortName; // ���Լ�ƣ��б���ʾ�ã�
	private int icon; // ����ͼ��
	private boolean haveIcon; // �Ƿ�ӵ��ͼ��
	
	public PersonAttr(String name , int number , String color , int icon , boolean haveIcon , String shortName) {
		this.name = name;
		this.number = number;
		this.color = color;
		this.icon = icon;
		this.haveIcon = haveIcon;
		this.shortName = shortName;
	}
	
	public String getName() {
		return name;
	}
	
	public int getNumber() {
		return number;
	}
	
	public String getColor() {
		return color;
	}
	
	public int getIcon() {
		return icon;
	}
	
	public boolean isHaveIcon() {
		return haveIcon;
	}
	
	public String getShortName() {
		return shortName;
	}

	public void setNumber(int number) {
		this.number = number;
	}
}
 