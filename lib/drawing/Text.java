package drawing;

import static org.lwjgl.opengl.GL11.*;

public class Text {

	public static final boolean[][][] mat = {
		{{false,true,true,true,true,true,true,false},
		 {false,true,false,false,false,false,true,false},	
		 {false,true,false,false,false,false,true,false},
		 {false,true,false,false,false,false,true,false},
		 {false,true,false,false,false,false,true,false},
		 {false,true,false,false,false,false,true,false},
		 {false,true,false,false,false,false,true,false},
		 {false,true,true,true,true,true,true,false}
		},
		{{false,false,false,false,true,false,false,false},
		 {false,false,false,true,true,false,false,false},	
		 {false,false,true,false,true,false,false,false},
		 {false,false,false,false,true,false,false,false},
		 {false,false,false,false,true,false,false,false},
		 {false,false,false,false,true,false,false,false},
		 {false,false,false,false,true,false,false,false},
		 {false,false,true,true,true,true,true,false}
		},
		{{false,false,false,true,true,true,false,false},
		 {false,false,true,false,false,false,true,false},	
		 {false,true,false,false,false,false,true,false},
		 {false,false,false,false,false,true,false,false},
		 {false,false,false,false,true,false,false,false},
		 {false,false,false,true,false,false,false,false},
		 {false,false,true,false,false,false,false,false},
		 {false,true,true,true,true,true,true,false}
		},
		{{false,false,true,true,true,true,false,false},
		 {false,false,false,false,false,false,true,false},	
		 {false,false,false,false,false,false,true,false},
		 {false,false,false,true,true,true,false,false},
		 {false,false,false,false,false,false,true,false},
		 {false,false,false,false,false,false,true,false},
		 {false,false,false,false,false,false,true,false},
		 {false,false,true,true,true,true,false,false}
		},
		
		{{false,false,true,false,false,true,false,false},
		 {false,false,true,false,false,true,false,false},	
		 {false,false,true,false,false,true,false,false},
		 {false,false,true,true,true,true,false,false},
		 {false,false,false,false,false,true,false,false},
		 {false,false,false,false,false,true,false,false},
		 {false,false,false,false,false,true,false,false},
		 {false,false,false,false,false,true,false,false}
		},
		{{false,false,true,true,true,true,true,false},
		 {false,false,true,false,false,false,false,false},	
		 {false,false,true,false,false,false,false,false},
		 {false,false,true,true,true,true,false,false},
		 {false,false,false,false,false,false,false,false},
		 {false,false,false,false,false,false,true,false},
		 {false,false,false,false,false,false,true,false},
		 {false,false,true,true,true,true,false,false}
		},
		{{false,false,false,false,false,true,false,false},
		 {false,false,false,false,true,false,false,false},	
		 {false,false,false,true,false,false,false,false},
		 {false,false,true,false,false,false,false,false},
		 {false,false,true,true,true,false,false,false},
		 {false,false,true,false,false,true,false,false},
		 {false,false,true,false,false,true,false,false},
		 {false,false,true,true,true,true,false,false}
		},
		{{false,true,true,true,true,true,true,false},
		 {false,false,false,false,false,false,true,false},	
		 {false,false,false,false,false,true,false,false},
		 {false,false,false,false,true,false,false,false},
		 {false,false,false,true,false,false,false,false},
		 {false,false,false,true,false,false,false,false},
		 {false,false,false,true,false,false,false,false},
		 {false,false,false,true,false,false,false,false}
		},
		{{false,false,true,true,true,true,false,false},
		 {false,true,false,false,false,false,true,false},	
		 {false,true,false,false,false,false,true,false},
		 {false,false,true,true,true,true,false,false},
		 {false,true,false,false,false,false,true,false},
		 {false,true,false,false,false,false,true,false},
		 {false,true,false,false,false,false,true,false},
		 {false,false,true,true,true,true,false,false}
		},
		{{false,false,true,true,true,false,false,false},
		 {false,true,false,false,false,true,false,false},	
		 {false,true,false,false,false,true,false,false},
		 {false,false,true,true,true,true,false,false},
		 {false,false,false,false,false,true,false,false},
		 {false,false,false,false,false,true,false,false},
		 {false,false,false,false,false,true,false,false},
		 {false,false,false,false,false,true,false,false}
		}
//		{{0,0,0,0,0,0,0,0},
//		 {0,0,0,0,0,0,0,0},
//		 {0,0,0,0,0,0,0,0},
//		 {0,0,0,0,0,0,0,0},
//		 {0,0,0,0,0,0,0,0},
//		 {0,0,0,0,0,0,0,0},
//		 {0,0,0,0,0,0,0,0},
//		 {0,0,0,0,0,0,0,0}
//		}
	};//mat
	
	public static int getWidth (String text){
		int len = text.length();
		
		if (len < 1) return 0;
		return 8*len + 2*(len-1);
	}//getWidth
	
	public static void drawText (String text){
		for (int i=0; i<text.length(); i++){
			char c = text.charAt(i);
			drawChar(c);
			glTranslatef(10, 0, 0);
		}
		glTranslatef(-text.length()*10,0,0);
	}//drawText
	
	public static void drawChar (char c){
		if (c < '0' || '9' < c) return;

		glBegin(GL_POINTS);

		int i = c-48;//Turns '0' into 0, through '9'->9
		for (int y=0; y<8; y++){
			for (int x=0; x<8; x++){
				if (mat[i][y][x]) glVertex2i(x,y);
			}
		}
		glEnd();
		
	}//drawChar
	
}//Text class
