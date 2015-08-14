package spanishblackjack.card;

import android.graphics.Bitmap;

public class Card
{
	private int cardId;
	private int cardValue;
	protected Bitmap cardBitmap;
	
	
	
	public Card(int cardId)
	{
		
		this.cardId = cardId;
	}
	public Bitmap getCardBitmap()
	{
		return cardBitmap;
	}
	public void setCardBitmap(Bitmap cardBitmap)
	{
		this.cardBitmap = cardBitmap;
	}
	public int getCardId()
	{
		return cardId;
	}
	public void setCardId(int cardId)
	{
		this.cardId = cardId;
	}
	
	public int getCardValue()
	{
		return cardValue;
	}
	public void setCardValue(int cardValue)
	{
		this.cardValue = cardValue;
	}
	
	

}
