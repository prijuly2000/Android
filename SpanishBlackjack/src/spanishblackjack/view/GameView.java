package spanishblackjack.view;

import java.util.ArrayList;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import spanishblackjack.activity.R;
import spanishblackjack.card.Card;
import spanishblackjack.card.FCard;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
@SuppressWarnings("unused")

public class GameView extends View
{
	private List<Card> playerHand1=new ArrayList<Card>();
	private List<Card> playerHand2=new ArrayList<Card>();
	private List<Card> dealerHand=new ArrayList<Card>();
	private List<Card> deck=new ArrayList<Card>();
	private final int NO_OF_DECKS=1; // Number of decks used in the game
	private final int MAX_RANK=115;
	private Context myContext;
	private int scaledCardW,scaledCardH,screenW,screenH;
	
	private int dealerScore=0,playerScore1=0,playerScore2=0,totalScore=0; // score for each hand
	private FCard flightCard;
	private float scale;
	private Paint paint;
	private Bitmap deckCover;
	private boolean split=false,passPress=false;
	private int turn=1; //0-dealer turn,1-player hand 1 turn,2- player hand 2 turn, (-1) - everyone played,decision needed for winner
	private Bitmap passUpBmp,passDownBmp;
	private int no_of_draws; // Number of cards to be drawn by the dealer 

		
		
	public GameView(Context context)
	{
		super(context);
		myContext=context;
		// TODO Auto-generated constructor stub
		setBackgroundColor(Color.rgb(0, 135, 0));
		scale=myContext.getResources().getDisplayMetrics().density;
	
		
		
		paint=new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setTextAlign(Paint.Align.LEFT);
		paint.setTextSize(scale*15);
		
		System.out.println("GameView");
		
	}
	
	private void initDeck()
	{
		//Intialize the deck(s) 
		System.out.println("initDeck");
		
		int tempId;
		int tempValue;
		int tempBitmapId;
		Bitmap tempBitmap;
		
		scaledCardW = (int) (screenW/8);
        scaledCardH = (int) (scaledCardW*1.28);
        
		
		// TODO Auto-generated method stub
		for(int k=0;k<NO_OF_DECKS;k++)
		{
			for(int i=0;i<4;i++)
			{
				tempValue=2;
				for(int j=102;j<MAX_RANK;j++)
				{
					if(j==110)   //Remove 10 cards from decks
						continue;
					
					tempId=(i*100)+j;
					tempBitmapId=getResources().getIdentifier("card"+tempId, "drawable", myContext.getPackageName());
					tempBitmap=BitmapFactory.decodeResource(getResources(), tempBitmapId);
					
			        
			        Card tempCard=new Card(tempId);
			        //Scale the cards
			        Bitmap scaledBitmap = Bitmap.createScaledBitmap(tempBitmap, scaledCardW, scaledCardH, false);
			        tempCard.setCardBitmap(scaledBitmap);
			        tempCard.setCardValue(tempValue);
					deck.add(tempCard);
									
					System.out.println("TempValue:"+tempValue);
					if(tempValue!=10 || j==MAX_RANK-2)
						tempValue++;
					
					
				}
			}
			//System.out.println("Deck "+(k+1)+" Initialized");
			
			
		}
		
		tempBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.card_back);
		deckCover=Bitmap.createScaledBitmap(tempBitmap, scaledCardW, scaledCardH, false);
		
		tempBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.pass_up);
		passUpBmp=Bitmap.createScaledBitmap(tempBitmap,scaledCardW,scaledCardH/4,false);
		
		tempBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.pass_down);
		passDownBmp=Bitmap.createScaledBitmap(tempBitmap,scaledCardW,scaledCardH/4,false);
		
		
	}
	
	private void newHand()
	{
		//For next hand, reset all the parameters
		System.out.println("newHand");
		playerScore1=0;
		playerScore2=0;
		dealerScore=0;
		turn=1;	
		split=false;
		
		deck.addAll(playerHand1);
		deck.addAll(playerHand2);
		deck.addAll(dealerHand);
		playerHand1.clear();
		playerHand2.clear();
		dealerHand.clear();
		
		dealCards();
		
	}
	
	

	
	private void dealCards()
	{
		//Deal cards at the begining
		System.out.println("dealCards");
		// TODO Auto-generated method stub
		no_of_draws=new Random().nextInt(3); //no of draws dealer will be making
		
		for(int i=0;i<2;i++)
			Collections.shuffle(deck,new Random());
		
		for(int i=0;i<2;i++)
		{
			drawCard(playerHand1);
			drawCard(dealerHand);
		}
		if(!split && playerHand1.get(0).getCardId()== playerHand1.get(1).getCardId())
		{
			showSplitHandDialog();
		}
		else
			updateScores();
		
	}

	private void drawCard(List<Card> hand)
	{
		System.out.println("drawCard");
		// TODO Auto-generated method stub
		hand.add(hand.size(),deck.get(0));
		deck.remove(0);
		
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		System.out.println("onDraw");
		
		canvas.drawText("Balance :"+playerScore1, 10,screenH-paint.getTextScaleX()-10, paint);
		if(!passPress)
			canvas.drawBitmap(passUpBmp, screenW/2-passUpBmp.getWidth()/2,screenH-passUpBmp.getHeight()-5 ,null);
		else
			canvas.drawBitmap(passDownBmp,screenW/2-passUpBmp.getWidth()/2,screenH-passUpBmp.getHeight()-5 ,null);
		
		for(int i=0;i<dealerHand.size();i++)
		{
			
			if(turn>0 && i==(dealerHand.size()-1))
				canvas.drawBitmap(deckCover,i*(scaledCardW+5),paint.getTextSize()+(20*scale),null);
			else
				canvas.drawBitmap(dealerHand.get(i).getCardBitmap(),i*(scaledCardW+5),paint.getTextSize()+(20*scale),null);
			
		}
		//simulating Shoe for drawing cards
		canvas.drawBitmap(deckCover, (screenW/2)-(deckCover.getWidth()/2),(screenH/2)-(deckCover.getHeight()/2),null);
		
		for(int i=0;i<playerHand1.size();i++)
		{
			canvas.drawBitmap(playerHand1.get(i).getCardBitmap(),
					i*(scaledCardW+5),screenH-scaledCardH-paint.getTextSize()-(20*scale),null);
		
		}
		
		if(split)
		{
			for(int i=0;i<playerHand2.size();i++)
			{
				canvas.drawBitmap(playerHand2.get(i).getCardBitmap(),
						i*(scaledCardW+5),screenH-(scaledCardH*2)-paint.getTextSize()-(20*scale),null);				
			}
			
		}	
		
		if(flightCard != null) 
		{
			
			flightCard.draw(canvas);

			if(flightCard.hasArrived())
			{
				flightCard = null;
				updateScores();
				if(turn!=0)
				checkHands();
			}
			
		}
		System.out.println("Dealer No of draws: "+no_of_draws);
		System.out.println("Turn : "+turn);
		if(turn==0 && flightCard==null )
			makeDealerPlay();
	
	
		invalidate();
		
	}
	
	private void checkHands()
	{
		System.out.println("checkHand");
		if(playerScore1>21 && !playerHand2.isEmpty())
		{
			Toast.makeText(myContext, "You lost 1st hand",Toast.LENGTH_SHORT).show();
			playerScore1=0;
			playerHand1.clear();
					
		}
		else if(split && playerScore2>21 && !playerHand1.isEmpty())
		{
			Toast.makeText(myContext,"You lost 2nd hand",Toast.LENGTH_SHORT).show();
			playerHand2.clear();
			playerScore2=0;
			
		}
		else if(playerScore1>21 || playerScore2>21 ||dealerScore>21 || turn==-1)
		{
			endHand();
			
		}
		
			
	}
	
	private void flyingCard(List<Card> playingHand)
	{
		if(flightCard!=null)
			return;
		
		System.out.println("flyingCard");
		
		
		System.out.println("Intended strt point:"+((screenW/2)-(deckCover.getWidth()/2))+", "+((screenH/2)-(deckCover.getHeight()/2)));
		Point dest=null;
		if(turn==2)
		{
			dest=new Point((int)(screenW-scaledCardW),(int)(screenH-(scaledCardH*2)-paint.getTextSize()-(20*scale)));
		}
		else if(turn==1)
		{
			System.out.println("dest created");
			dest=new Point(0,(int)(screenH-scaledCardH-paint.getTextSize()-(20*scale)));
		}
		else if(turn ==0)
		{
			dest=new Point(0,(int)(paint.getTextSize()+(20*scale)));
		}
		
		ArrayList<Card> hand=new ArrayList<Card>();
		drawCard(hand);
		
		Point start=new Point((screenW/2)-(deckCover.getWidth()/2), (screenH/2)-(deckCover.getHeight()/2));
		
		Card card=hand.get(0);
		
		System.out.println("Real inflighht card value:"+card.getCardValue());
		flightCard=new FCard(card, start, dest,playingHand);
		
		
		return;
		
	}
	
	private void endHand()
	{
		System.out.println("endHand");
		
		totalScore+=playerScore1+playerScore2;
		
		System.out.println("End Hand Player Score 1: "+playerScore1);
		final Dialog endHandDialog=new Dialog(myContext);
		
		endHandDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		endHandDialog.setContentView(R.layout.end_hand_dialog);
		
		Button nxtBtn=(Button)endHandDialog.findViewById(R.id.nextHandButton);
		nxtBtn.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				newHand();
				endHandDialog.dismiss();			
			
			}
		});
		
		//Check if player or computer wins
		TextView tv=(TextView)endHandDialog.findViewById(R.id.endHandText);
		if(dealerScore>21 || dealerScore<playerScore1 || dealerScore<playerScore2) // 
			tv.setText("   Congrats!!You won  ");
		else if(dealerScore==playerScore1 || dealerScore==playerScore2)
			tv.setText("  It's a tie  ");
		else 
			tv.setText("  You lost the hand!!  ");
		
		endHandDialog.show();
		
		
	}
	
	private void updateScores()
	{
		System.out.println("updateScores");
		playerScore1=playerScore2=dealerScore=0;
		//System.out.println("Player1 hand size"+playerHand1.size());
		for (int i = 0; i < playerHand1.size(); i++) 
		{
			playerScore1+=playerHand1.get(i).getCardValue();
			//System.out.println("playerScore1:"+playerScore1);
		}
		for (int i = 0; i < dealerHand.size(); i++) 
		{
			dealerScore+=dealerHand.get(i).getCardValue();
			
		}
		for (int i = 0; i < playerHand2.size(); i++) 
		{
			playerScore2+=playerHand2.get(i).getCardValue();						
		}
		
		
	}

	private void showSplitHandDialog()
	{
		// TODO Auto-generated method stub
		final Dialog splitHandDialog=new Dialog(myContext);
		
		splitHandDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		splitHandDialog.setContentView(R.layout.split_dialog);
		Button yesBtn=(Button)splitHandDialog.findViewById(R.id.yesButton);
		yesBtn.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				
				splitHand();
				splitHandDialog.dismiss();
				
			}

			
		});
		 
		Button noBtn=(Button)splitHandDialog.findViewById(R.id.noButton);
		noBtn.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				
				splitHandDialog.dismiss();
				
			}
		});
		splitHandDialog.show();
		split=true;
		
	}
	private void splitHand()
	{
		// TODO Auto-generated method stub
		// Split the player hand into 2 hands & update the scores of both hands
		playerHand2.add(playerHand1.get(0));
		playerHand1.remove(0);
		
		turn=2;
		updateScores();
		
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		
		screenW=w;
		screenH=h;
		
        initDeck();
        dealCards();
        
        //System.out.println("onSizeChanged");
	}
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		System.out.println("onTouchEvent");
		// TODO Auto-generated method stub
		int actionevent=event.getAction();
		int x=(int)event.getX();
		int y=(int)event.getY();
		
		switch(actionevent)
		{
		case MotionEvent.ACTION_DOWN:
			if(x>screenW/2-passUpBmp.getWidth()/2
					&& x<screenW/2+passUpBmp.getWidth()/2
					&& y>screenH-passUpBmp.getHeight()-5 
					&& y<screenH-5)
			{
				passPress=true;
				if(turn==2) //current Play hand 2 play
					turn=1; //Player hand 1 play
				else if(turn==1)
				{
					turn=0; //computer's play
					
				}
				//System.out.println("Turn: "+turn);
			}
				
			break;
		case MotionEvent.ACTION_UP:
			// Click on the stock pile for hit
			
			if(x>(screenW/2-deckCover.getWidth()/2) 
					&& x<(screenW/2+deckCover.getWidth()/2)
					&& y>(screenH/2-deckCover.getHeight()/2)
					&& y<(screenH/2+deckCover.getHeight()/2))
			{
				if(turn==2)       //player hand 2 play  
				{			
						System.out.println("Player hand 2");
						flyingCard(playerHand2);				
				}
				else if(turn ==1) // player hand 1 play
				{
					System.out.println("Player hand 1");
					flyingCard(playerHand1);
					
				}
				else if(turn ==0)
				{
					Toast.makeText(myContext, " Wait.. Dealer's Turn ", Toast.LENGTH_SHORT).show();
					makeDealerPlay();					
				}
			}
			passPress=false;			
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		
		}
		
		
		invalidate();
		return true;
	}

	private void makeDealerPlay()
	{
		// TODO Auto-generated method stub
		//If the cards are already drawn by the computer  then return otherwise draw the card 
		if(dealerHand.size()-2==no_of_draws || dealerScore>21)
		{
			turn=-1;
			checkHands();
			return;
		}
		
					
		flyingCard(dealerHand);		
			
		
		
	}
	
	
	

}
