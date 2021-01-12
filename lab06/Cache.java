public class Cache {

    private int size;
    private int blockSize;
    private int associativity;
    private int[][] array;
	private int[][] array2;
    private int indices;
    private int hits = 0;
    private int misses = 0;
	private int x;
	private int [][] extras;

    public Cache(int totalSize, int blockSize, int associativity){
        this.size = totalSize;
        this.blockSize = blockSize;
        this.associativity = associativity;
        this.indices = totalSize / (4 * blockSize * associativity);
    }
    /* Initializes the cache array */
    public void initializeCache(){
        array = new int[indices][associativity];
		array2 = new int[indices][associativity];
		if(blockSize > 1){
			extras = new int[4][blockSize];
		}
    }

    public void compute(int address){
        /* Acquire the cache index from the address */
        int cacheAddress = (address/blockSize) % indices;

        /* If Direct Mapped with a Block Size of 1 ( Caches 1 and 7 )*/
        if ((blockSize == 1) && (associativity == 1)){
            /* If the cache does not hold the value of this address...*/
            if (array[cacheAddress][0] != address){
                /* Set it as the new value */
                array[cacheAddress][0] = address;
                /* Increment number of misses */
                misses++;
            }
            else{
                /* Increment number of hits */
                hits++;
            }
        }
        else{
            /* If Direct Mapped and Block Size > 1 ( Caches 2 and 3 )*/
            if (associativity == 1){
                /* Different way of checking all word blocks in the cache index */
                /* While only storing one value */
                /* Works perfectly, you can change it but the space complexity would suffer */
                int addressValue = array[cacheAddress][0] - address;
                if (Math.abs(addressValue) >= blockSize){
                    /* Update our cache boi. */
                    array[cacheAddress][0] = address;
                    /* Take that L and update the misses. */
                    misses++;
                }
                else{
                    /* I guess they never miss, huh? */
                    hits++;
                }
            }

            /* If Associative... Implement caches 4,5,6 below*/
			if (associativity == 2){					
				
                if ((array[cacheAddress][0] != address) && ( array[cacheAddress][1] != address)){
                    /* Update our cache boi. */
					if(array[cacheAddress][0] == 0){
						array[cacheAddress][0] = address;
						array2[cacheAddress][0] = 1;
						array2[cacheAddress][1] = 0;
					}else if(array[cacheAddress][1] == 0){
						array[cacheAddress][1] = address;
						array2[cacheAddress][0] = 0;
						array2[cacheAddress][1] = 1;
					}else if(array2[cacheAddress][0] == 0){
						array[cacheAddress][0] = address;
						array2[cacheAddress][0] = 1;
						array2[cacheAddress][1] = 0;
					}else{
						array[cacheAddress][1] = address;
						array2[cacheAddress][0] = 0;
						array2[cacheAddress][1] = 1;
					}
                    /* Take that L and update the misses. */
                    misses++;
				}else{
                    /* I guess they never miss, huh? */
					if(array[cacheAddress][0] == address){
						array2[cacheAddress][0] = 1;
						array2[cacheAddress][1] = 0;
					}else{
						array2[cacheAddress][0] = 0;
						array2[cacheAddress][1] = 1;
					}
                    hits++;
                }
			}
			
			if (associativity == 4 && blockSize == 4){
				
				for(int w = 0; w < 4; w++){
					for(int q = 0; q < 4; q++){
						extras[w][q] = array[cacheAddress][w] + q;
					}
				}
				
								
				if ((extras[0][0] != address) && (extras[0][1] != address) && (extras[0][2] != address) &&
				(extras[0][3] != address) && (extras[1][0] != address) && (extras[1][1] != address) &&
				(extras[1][2] != address) && (extras[1][3] != address) && (extras[2][0] != address) &&
				(extras[2][1] != address) && (extras[2][2] != address) && (extras[2][3] != address) &&
				(extras[3][0] != address) && (extras[3][1] != address) && (extras[3][2] != address) &&
				(extras[3][3] != address)){
                    /* Update our cache boi. */
					if(array2[cacheAddress][0] == 0){
						array[cacheAddress][0] = address;
						array2[cacheAddress][3] = array2[cacheAddress][2];
						array2[cacheAddress][2] = array2[cacheAddress][1];
						array2[cacheAddress][1] = array2[cacheAddress][0];
						array2[cacheAddress][0] = address;
					}else if(array2[cacheAddress][1] == 0){
						array[cacheAddress][1] = address;
						array2[cacheAddress][3] = array2[cacheAddress][2];
						array2[cacheAddress][2] = array2[cacheAddress][1];
						array2[cacheAddress][1] = array2[cacheAddress][0];
						array2[cacheAddress][0] = address;
					}else if(array2[cacheAddress][2] == 0){
						array[cacheAddress][2] = address;
						array2[cacheAddress][3] = array2[cacheAddress][2];
						array2[cacheAddress][2] = array2[cacheAddress][1];
						array2[cacheAddress][1] = array2[cacheAddress][0];
						array2[cacheAddress][0] = address;
					}else if(array2[cacheAddress][3] == 0){
						array[cacheAddress][3] = address;
						array2[cacheAddress][3] = array2[cacheAddress][2];
						array2[cacheAddress][2] = array2[cacheAddress][1];
						array2[cacheAddress][1] = array2[cacheAddress][0];
						array2[cacheAddress][0] = address;
					}else if(array[cacheAddress][0] == array2[cacheAddress][3]){
						array[cacheAddress][0] = address;
						array2[cacheAddress][3] = array2[cacheAddress][2];
						array2[cacheAddress][2] = array2[cacheAddress][1];
						array2[cacheAddress][1] = array2[cacheAddress][0];
						array2[cacheAddress][0] = address;
					}else if(array[cacheAddress][1] == array2[cacheAddress][3]){
						array[cacheAddress][1] = address;
						array2[cacheAddress][3] = array2[cacheAddress][2];
						array2[cacheAddress][2] = array2[cacheAddress][1];
						array2[cacheAddress][1] = array2[cacheAddress][0];
						array2[cacheAddress][0] = address;
					}else if(array[cacheAddress][2] == array2[cacheAddress][3]){
						array[cacheAddress][2] = address;
						array2[cacheAddress][3] = array2[cacheAddress][2];
						array2[cacheAddress][2] = array2[cacheAddress][1];
						array2[cacheAddress][1] = array2[cacheAddress][0];
						array2[cacheAddress][0] = address;
					}else{
						array[cacheAddress][3] = address;
						array2[cacheAddress][3] = array2[cacheAddress][2];
						array2[cacheAddress][2] = array2[cacheAddress][1];
						array2[cacheAddress][1] = array2[cacheAddress][0];
						array2[cacheAddress][0] = address;
					}
                    /* Take that L and update the misses. */
                    misses++;
				}else{
                    /* I guess they never miss, huh? */
					x = 0;
					
					if((extras[0][0] == address) || (extras[0][1] == address) || (extras[0][2] == address) ||
				(extras[0][3] == address)){
						
						while(x < 4){
							if(array2[cacheAddress][x] == array[cacheAddress][0]){
								if(x == 0){
									x = 5;
								}else if(x == 1){
									array2[cacheAddress][1] = array2[cacheAddress][0];
									array2[cacheAddress][0] = array[cacheAddress][0];
									x = 5;
								}else if(x == 2){
									array2[cacheAddress][2] = array2[cacheAddress][1];
									array2[cacheAddress][1] = array2[cacheAddress][0];
									array2[cacheAddress][0] = array[cacheAddress][0];
									x = 5;
								}else if(x == 3){
									array2[cacheAddress][3] = array2[cacheAddress][2];
									array2[cacheAddress][2] = array2[cacheAddress][1];
									array2[cacheAddress][1] = array2[cacheAddress][0];
									array2[cacheAddress][0] = array[cacheAddress][0];
									x = 5;
								}
							}
							x++;
						}
						
					}else if((extras[1][0] == address) || (extras[1][1] == address) || (extras[1][2] == address) ||
				(extras[1][3] == address)){
						while(x < 4){
							if(array2[cacheAddress][x] == array[cacheAddress][1]){
								if(x == 0){
									x = 5;
								}else if(x == 1){
									array2[cacheAddress][1] = array2[cacheAddress][0];
									array2[cacheAddress][0] = array[cacheAddress][1];
									x = 5;
								}else if(x == 2){
									array2[cacheAddress][2] = array2[cacheAddress][1];
									array2[cacheAddress][1] = array2[cacheAddress][0];
									array2[cacheAddress][0] = array[cacheAddress][1];
									x = 5;
								}else if(x == 3){
									array2[cacheAddress][3] = array2[cacheAddress][2];
									array2[cacheAddress][2] = array2[cacheAddress][1];
									array2[cacheAddress][1] = array2[cacheAddress][0];
									array2[cacheAddress][0] = array[cacheAddress][1];
									x = 5;
								}
							}
							x++;
						}
					}else if((extras[2][0] == address) || (extras[2][1] == address) || (extras[2][2] == address) ||
				(extras[2][3] == address)){
						while(x < 4){
							if(array2[cacheAddress][x] == array[cacheAddress][2]){
								if(x == 0){
									x = 5;
								}else if(x == 1){
									array2[cacheAddress][1] = array2[cacheAddress][0];
									array2[cacheAddress][0] = array[cacheAddress][2];
									x = 5;
								}else if(x == 2){
									array2[cacheAddress][2] = array2[cacheAddress][1];
									array2[cacheAddress][1] = array2[cacheAddress][0];
									array2[cacheAddress][0] = array[cacheAddress][2];
									x = 5;
								}else if(x == 3){
									array2[cacheAddress][3] = array2[cacheAddress][2];
									array2[cacheAddress][2] = array2[cacheAddress][1];
									array2[cacheAddress][1] = array2[cacheAddress][0];
									array2[cacheAddress][0] = array[cacheAddress][2];
									x = 5;
								}
							}
							x++;
						}
					}else{
						while(x < 4){
							if(array2[cacheAddress][x] == array[cacheAddress][3]){
								if(x == 0){
									x = 5;
								}else if(x == 1){
									array2[cacheAddress][1] = array2[cacheAddress][0];
									array2[cacheAddress][0] = array[cacheAddress][3];
									x = 5;
								}else if(x == 2){
									array2[cacheAddress][2] = array2[cacheAddress][1];
									array2[cacheAddress][1] = array2[cacheAddress][0];
									array2[cacheAddress][0] = array[cacheAddress][3];
									x = 5;
								}else if(x == 3){
									array2[cacheAddress][3] = array2[cacheAddress][2];
									array2[cacheAddress][2] = array2[cacheAddress][1];
									array2[cacheAddress][1] = array2[cacheAddress][0];
									array2[cacheAddress][0] = array[cacheAddress][3];
									x = 5;
								}
							}
							x++;
						}						
					}
                    hits++;
                }
				
			}
			
			if(associativity == 4 && blockSize == 1){
								
				if ((array[cacheAddress][0] != address) && (array[cacheAddress][1] != address)
					&& (array[cacheAddress][2] != address) && (array[cacheAddress][3] != address)){
                    /* Update our cache boi. */
					if(array2[cacheAddress][0] == 0){
						array[cacheAddress][0] = address;
						array2[cacheAddress][3] = array2[cacheAddress][2];
						array2[cacheAddress][2] = array2[cacheAddress][1];
						array2[cacheAddress][1] = array2[cacheAddress][0];
						array2[cacheAddress][0] = address;
					}else if(array2[cacheAddress][1] == 0){
						array[cacheAddress][1] = address;
						array2[cacheAddress][3] = array2[cacheAddress][2];
						array2[cacheAddress][2] = array2[cacheAddress][1];
						array2[cacheAddress][1] = array2[cacheAddress][0];
						array2[cacheAddress][0] = address;
					}else if(array2[cacheAddress][2] == 0){
						array[cacheAddress][2] = address;
						array2[cacheAddress][3] = array2[cacheAddress][2];
						array2[cacheAddress][2] = array2[cacheAddress][1];
						array2[cacheAddress][1] = array2[cacheAddress][0];
						array2[cacheAddress][0] = address;
					}else if(array2[cacheAddress][3] == 0){
						array[cacheAddress][3] = address;
						array2[cacheAddress][3] = array2[cacheAddress][2];
						array2[cacheAddress][2] = array2[cacheAddress][1];
						array2[cacheAddress][1] = array2[cacheAddress][0];
						array2[cacheAddress][0] = address;
					}else if(array[cacheAddress][0] == array2[cacheAddress][3]){
						array[cacheAddress][0] = address;
						array2[cacheAddress][3] = array2[cacheAddress][2];
						array2[cacheAddress][2] = array2[cacheAddress][1];
						array2[cacheAddress][1] = array2[cacheAddress][0];
						array2[cacheAddress][0] = address;
					}else if(array[cacheAddress][1] == array2[cacheAddress][3]){
						array[cacheAddress][1] = address;
						array2[cacheAddress][3] = array2[cacheAddress][2];
						array2[cacheAddress][2] = array2[cacheAddress][1];
						array2[cacheAddress][1] = array2[cacheAddress][0];
						array2[cacheAddress][0] = address;
					}else if(array[cacheAddress][2] == array2[cacheAddress][3]){
						array[cacheAddress][2] = address;
						array2[cacheAddress][3] = array2[cacheAddress][2];
						array2[cacheAddress][2] = array2[cacheAddress][1];
						array2[cacheAddress][1] = array2[cacheAddress][0];
						array2[cacheAddress][0] = address;
					}else{
						array[cacheAddress][3] = address;
						array2[cacheAddress][3] = array2[cacheAddress][2];
						array2[cacheAddress][2] = array2[cacheAddress][1];
						array2[cacheAddress][1] = array2[cacheAddress][0];
						array2[cacheAddress][0] = address;
					}
                    /* Take that L and update the misses. */
                    misses++;
				}else if(associativity == 4){
                    /* I guess they never miss, huh? */
					x = 0;
					
					if(array[cacheAddress][0] == address){
						
						while(x < 4){
							if(array2[cacheAddress][x] == array[cacheAddress][0]){
								if(x == 0){
									x = 5;
								}else if(x == 1){
									array2[cacheAddress][1] = array2[cacheAddress][0];
									array2[cacheAddress][0] = array[cacheAddress][0];
									x = 5;
								}else if(x == 2){
									array2[cacheAddress][2] = array2[cacheAddress][1];
									array2[cacheAddress][1] = array2[cacheAddress][0];
									array2[cacheAddress][0] = array[cacheAddress][0];
									x = 5;
								}else if(x == 3){
									array2[cacheAddress][3] = array2[cacheAddress][2];
									array2[cacheAddress][2] = array2[cacheAddress][1];
									array2[cacheAddress][1] = array2[cacheAddress][0];
									array2[cacheAddress][0] = array[cacheAddress][0];
									x = 5;
								}
							}
							x++;
						}
						
					}else if(array[cacheAddress][1] == address){
						while(x < 4){
							if(array2[cacheAddress][x] == array[cacheAddress][1]){
								if(x == 0){
									x = 5;
								}else if(x == 1){
									array2[cacheAddress][1] = array2[cacheAddress][0];
									array2[cacheAddress][0] = array[cacheAddress][1];
									x = 5;
								}else if(x == 2){
									array2[cacheAddress][2] = array2[cacheAddress][1];
									array2[cacheAddress][1] = array2[cacheAddress][0];
									array2[cacheAddress][0] = array[cacheAddress][1];
									x = 5;
								}else if(x == 3){
									array2[cacheAddress][3] = array2[cacheAddress][2];
									array2[cacheAddress][2] = array2[cacheAddress][1];
									array2[cacheAddress][1] = array2[cacheAddress][0];
									array2[cacheAddress][0] = array[cacheAddress][1];
									x = 5;
								}
							}
							x++;
						}
					}else if(array[cacheAddress][2] == address){
						while(x < 4){
							if(array2[cacheAddress][x] == array[cacheAddress][2]){
								if(x == 0){
									x = 5;
								}else if(x == 1){
									array2[cacheAddress][1] = array2[cacheAddress][0];
									array2[cacheAddress][0] = array[cacheAddress][2];
									x = 5;
								}else if(x == 2){
									array2[cacheAddress][2] = array2[cacheAddress][1];
									array2[cacheAddress][1] = array2[cacheAddress][0];
									array2[cacheAddress][0] = array[cacheAddress][2];
									x = 5;
								}else if(x == 3){
									array2[cacheAddress][3] = array2[cacheAddress][2];
									array2[cacheAddress][2] = array2[cacheAddress][1];
									array2[cacheAddress][1] = array2[cacheAddress][0];
									array2[cacheAddress][0] = array[cacheAddress][2];
									x = 5;
								}
							}
							x++;
						}
					}else{
						while(x < 4){
							if(array2[cacheAddress][x] == array[cacheAddress][3]){
								if(x == 0){
									x = 5;
								}else if(x == 1){
									array2[cacheAddress][1] = array2[cacheAddress][0];
									array2[cacheAddress][0] = array[cacheAddress][3];
									x = 5;
								}else if(x == 2){
									array2[cacheAddress][2] = array2[cacheAddress][1];
									array2[cacheAddress][1] = array2[cacheAddress][0];
									array2[cacheAddress][0] = array[cacheAddress][3];
									x = 5;
								}else if(x == 3){
									array2[cacheAddress][3] = array2[cacheAddress][2];
									array2[cacheAddress][2] = array2[cacheAddress][1];
									array2[cacheAddress][1] = array2[cacheAddress][0];
									array2[cacheAddress][0] = array[cacheAddress][3];
									x = 5;
								}
							}
							x++;
						}						
					}
                    hits++;
                }
				
			}
        }

    }

    public void printStatistics(){
        /* Acquire hit rate*/
        float accuracyPercentage = ((float)hits / (float)(hits + misses)) * 100;
        /* Print our statistics */
        System.out.println("Cache size: "+ size +"B\t\tAssociativity: "+ associativity + "\t\tBlock size: "+ blockSize);
        System.out.println("Hits: "+hits+"\tHit Rate: "+ String.format("%.2f", accuracyPercentage)+"%");
    }
}
