package spanishblackjack.view;

import spanishblackjack.activity.GameActivity;
import spanishblackjack.activity.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

public class TitleView extends View
{
	private Bitmap titlePicture;
	private Context myContext;
	private Bitmap playBtnUp,playBtnDown,optionsBtnUp,optionsBtnDown;
	private int screenW;
	private int screenH;
	private boolean playBtnPress=false,optionsBtnPress=false;

	public TitleView(Context context)
	{
		// TODO Auto-generated constructor stub
		
		super(context);

		myContext=context;
		playBtnUp=BitmapFactory.decodeResource(getResources(), R.drawable.play_up);
		playBtnDown=BitmapFactory.decodeResource(getResources(), R.drawable.play_down);
		optionsBtnUp=BitmapFactory.decodeResource(getResources(), R.drawable.options_up);
		optionsBtnDown=BitmapFactory.decodeResource(getResources(), R.drawable.options_down);
		titlePicture=BitmapFactory.decodeResource(getResources(), R.drawable.title_screen);
		
	}
	
	@Override
	protected void onDraw(final Canvas canvas)
	{
		
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.drawBitmap(titlePicture, 0,0 ,null);
		
		
		if(playBtnPress)
		{
			canvas.drawBitmap(playBtnDown, (screenW-playBtnUp.getWidth())/2,(int) (screenH*0.7),null);			
		}
		else
		{
			canvas.drawBitmap(playBtnUp, (screenW-playBtnDown.getWidth())/2, (int)(screenH*0.7),null);
		}
		if(optionsBtnPress)
		{
			canvas.drawBitmap(optionsBtnDown, (screenW-playBtnUp.getWidth())/2,(int)(screenH*0.85) , null);
		}
		else
		{
			canvas.drawBitmap(optionsBtnUp, (screenW-playBtnDown.getWidth())/2,(int)(screenH*0.85), null);
		}
	}


	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		// TODO Auto-generated method stub
		int action=event.getAction();
		int x=(int)event.getX();
		int y=(int)event.getY();
		
		switch(action)
		{
		case MotionEvent.ACTION_DOWN:
			if(x>(screenW-playBtnUp.getWidth())/2 &&
				x<((screenW-playBtnUp.getWidth())/2+playBtnUp.getWidth()) &&
				y>((int)screenH*0.7) && y<((int)screenH*0.7+playBtnUp.getHeight()))
			{
				playBtnPress=true;
			}
			if(x>(screenW-optionsBtnUp.getWidth())/2 &&
					x<((screenW-optionsBtnUp.getWidth())/2+optionsBtnUp.getWidth()) &&
					y>((int)screenH*0.85) && y<((int)(screenH*0.85+optionsBtnUp.getHeight())))
			{
				optionsBtnPress=true;
			}
			break;
		case MotionEvent.ACTION_UP:
			if(playBtnPress)
			{
				Intent gameIntent=new Intent(myContext,GameActivity.class);
				myContext.startActivity(gameIntent);
				
			}
			playBtnPress=false;
			optionsBtnPress=false;
			break;
		case MotionEvent.ACTION_MOVE:
			break;					
		
		}
		invalidate();
		return true;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		screenW=w;
		screenH=h;
		
	}
	
	

}
