package model;

/**
 * Created by Superxlcr
 * 用户属性类
 */
public class PersonAttr {
	private String name; // 属性名称
	private int number; // 属性值
	private String color; // 属性文字颜色
	private String shortName; // 属性简称（列表显示用）
	private int icon; // 属性图标
	private boolean haveIcon; // 是否拥有图标
	
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
 