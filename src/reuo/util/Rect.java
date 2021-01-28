package reuo.util;

public class Rect{
	public int top, left, bottom, right;
	
	public Rect(){
		this(0);
	}
	
	public Rect(Rect from){
		top = from.top;
		left = from.left;
		bottom = from.bottom;
		right = from.right;
	}
	
	public Rect(int padding){
		top = left = bottom = right = padding;
	}
	
	public Rect(int top, int left, int bottom, int right){
		//if(bottom > top){
			this.top = top;
			this.bottom = bottom;
		//}else{
		//	this.bottom = top;
		//	this.top = bottom;
		//}
		
		//if(right > left){
			this.right = right;
			this.left = left;
		//}else{
		//	this.right = left;
		//	this.left = right;
		//}
	}
	
	@Override
	public String toString(){
		return String.format("[%d, %d, %d, %d]", top, left, bottom, right);
	}
	
	public int getWidth(){
		return(right - left);
	}
	
	public int getHeight(){
		return (bottom - top);
	}
}
