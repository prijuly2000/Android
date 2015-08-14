package spanishblackjack.card;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;

public class FCard extends Card
{
	public final static int SPEED=25;
	private Point position=new Point(0,0);
	private Point dest;
	private List<Card> pile;
	private Card fCard;

	
	public FCard(Card card, Point start,Point dest,List<Card> pile)
	{
		super(card.getCardId());
		//System.out.println("Constructor Inflight card value:"+card.getCardValue());
		this.dest = dest;
		this.position = start;
		this.pile=pile;
		this.cardBitmap=card.cardBitmap;
		this.fCard=card;	
		
		
	}
	
	private void drawFaceCard(Canvas canvas,int x,int y)
	{
		Bitmap bmp=this.getCardBitmap();
		if(bmp!=null)
			canvas.drawBitmap(bmp, x, y, null);
	}
	
	public boolean hasArrived()
	{
		return (dest.x==position.x && dest.y==position.y); 
	}
	
	public void draw(Canvas canvas)
	{
		//System.out.println("FCard.draw");
		if(this.hasArrived())
		{
			return;
		}
		
		double sx=dest.x-position.x;
		double sy=dest.y-position.y;
		
		double s=Math.sqrt(sx*sx+sy*sy);
		double theta=Math.asin(sx/s);
		double dx=SPEED*Math.sin(theta)+0.5;
		
		double gamma=Math.acos(sy/s);
		double dy=SPEED*Math.cos(gamma)+0.5;
		
		position.x+=dx;
		position.y+=dy;
		
		if (Math.abs(dest.x - position.x) <= SPEED &&
	        	Math.abs(dest.y - position.y) <= SPEED) 
		{		
			
	        	pile.add(0,fCard);
	        	//System.out.println("FDraw Inflight card value:"+fCard.getCardValue());
	            position.x = dest.x;
	            position.y = dest.y;
	            
	        }
	        else
	        {
	        	        	
	        	drawFaceCard(canvas,position.x,position.y);
	        	
	        }
	}
	
	
	

}
