package bunk;

intention JunkIntention {
    
}

class SimpleClass implementsintention JunkIntention {
	
	public static void main(String[] args) {
		
		[[ x1 | Junk ]]
		int x = 5;
		
		[[ x1a | Junk2 ]]
		for (int y = 1; y < 5; y++) {
			System.out.println("y == " + y);
		}
		[[ /x1a ]]
		  
		[[ /x1 ]]
		 
		System.out.println("I am a sample Java program.");
	}

}
