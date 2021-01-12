# Name: James Plasko and Jesus Blanco
# Section: 03
# Description: Exponentiates a number, x by a power, y.
# Java code:
#public static int exponentiate(int raised, int rpower){	
#	int holder = raised;
#	int total = 0;
#	int i, j;
#	
#	for(i = 0; i < power; i++){
#		for(j = 0; j < raised; i++){
#			total += holder
#		}
#			holder = total;
#			total = 0;
#	}
#	total = holder;
#	return total
#}
# global variables

.globl welcome
.globl prompt
.globl leftOver

# Data
.data

welcome1:
        .asciiz " Enter number to be raised: "
		
second:
        .asciiz "\n Enter number for power: "

prompt:
        .asciiz "\n Final: "
.text

main:
    # Prompt for base
	ori $v0, $0, 4
	
	lui $a0, 0x1001
	syscall
	
	ori $v0, $0, 5
	syscall
	
	add $s0, $v0, $0
	
    # Prompt for exponent
	ori $v0, $0, 4
	
	lui $a0, 0x1001
	ori $a0, $a0, 29
	syscall
	
	ori $v0, $0, 5
	syscall
	
	add $s1, $v0, $0
    # CASE: POWER OF ZERO
    beq $s1, $0, zeroPower
    # CASE: POWER OF ONE
    addi $t3, $t3, 1
    beq $s1, $t3, onePower
    
	add $s2, $s0, $0
	add $s3, $s0, $0
	add $t1, $t1, $s0
	add $s1, $s1, -1
	add $s2, $s2, -1
    
	loopinside:
		add $t0, $t1, $t0
		add $s3, $s3, -1
		bne $s3, $0, loopinside
		add $t1, $t0, $0
		add $s3, $s2, $0
		addi $s1, $s1, -1
		bne $s1, $0, loopinside
		j finish
	
    onePower:
        add $t0, $0, $s0
        beq $0, $0, finish 
        
    zeroPower:
        addi $t0, $t0, 1
        
	finish:
	ori $v0, $0, 4
	
	lui $a0, 0x1001
	ori $a0, $a0, 56
    syscall
	
	ori $v0, $0, 1
	add $a0, $t0, $0
	syscall
	
    # Exit
    ori     $v0, $0, 10
    syscall
