// JPieceTest.java

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.event.*;


/**
 Debugging client for the Piece class.
 The JPieceTest component draws all the rotations of a tetris piece.
 JPieceTest.main()  creates a frame  with one JPieceTest for each
 of the 7 standard tetris pieces.
 
 This is the starter file version -- 
 The outer shell is done. You need to complete paintComponent()
 and drawPiece().
*/
class JPieceTest extends JComponent {
	protected Piece root;	
	

	public JPieceTest(Piece piece, int width, int height) {
		super();
		
		setPreferredSize(new Dimension(width, height));

		root = piece;
	}

	/**
	 Draws the rotations from left to right.
	 Each piece goes in its own little box.
	*/
	public final int MAX_ROTATIONS = 4;
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Rectangle fullArea = this.getBounds();
		
		int startX = 0;
		int partWidth = (int)(fullArea.getWidth()/MAX_ROTATIONS);		
		Piece original = root;
				
		for (int i = 0; i < MAX_ROTATIONS;i++)
		{
			drawPiece(g,root,new Rectangle(startX,0,partWidth,(int)fullArea.getHeight()));
			// change the dimension of the rectangle
			startX += partWidth;
			if (root.nextRotation().equals(original))
				break; // stop drawing once at the start
			
			root = root.nextRotation();
		}
	}
	
	/**
	 Draw the piece inside the given rectangle.
	*/
	private void drawPiece(Graphics g, Piece piece, Rectangle r) {
		
		int sqrDim = (int)(r.height/MAX_ROTATIONS);
		
		
		for (Point pt : piece.getBody()){
			
			int xCoord = (int)pt.getX()*sqrDim+r.x;
			int yCoord  = (MAX_ROTATIONS -1)*sqrDim - (int)pt.getY()*sqrDim;
			
			
			
			if (piece.getSkirt()[(int)pt.getX()] == (int)pt.getY())
			{
				g.setColor(Color.yellow); 
			}
			
			g.fillRect(xCoord ,yCoord ,sqrDim - 1 ,sqrDim-1);
			g.setColor(Color.black);
	}
		g.setColor(Color.red);
		g.drawString(String.format("w: %d h: %d", piece.getWidth(),piece.getHeight()), 1+r.x,4*sqrDim - 1);
		g.setColor(Color.black);
}


	/**
	 Draws all the pieces by creating a JPieceTest for
	 each piece, and putting them all in a frame.
	*/
	static public void main(String[] args)
	
	{
		JFrame frame = new JFrame("Piece Tester");
		JComponent container = (JComponent)frame.getContentPane();
		
		// Put in a BoxLayout to make a vertical list
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		
		Piece[] pieces = Piece.getPieces();
		
		for (int i=0; i<pieces.length; i++) {
			JPieceTest test = new JPieceTest(pieces[i], 375, 75);
			container.add(test);
		}
		
		// Size the window and show it on screen
		frame.pack();
		frame.setVisible(true);
		
		// Quit on window close
		frame.addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			}
		);
	}
}