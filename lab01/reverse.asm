# Name:  Jesus Blanco and James Plasko
# Section:  CPE 315-03
# Description: Reverses the bits in a binary number
# Java code:
#public static int reverse(int num){
#    int temp;
#    int reverse = 0;
#
#    for(int i = 1; i<= 31; i++){
#        temp = 1;
#        temp = num & temp;
#        reverse = (reverse ^ temp) << 1;
#        num = num >> 1;
#    }
#
#return reverse;
#}

.globl welcome
.globl prompt
.globl reverseText

.data

welcome:
    .asciiz " This program reverses the bits in a binary number.\n\n"

prompt:
    .asciiz "  Enter an integer: "

reverseText:
    .asciiz " \n Reversed = "
    
.text

main:

    # Display welcome message
    ori     $v0, $0, 4
    lui     $a0, 0x1001
    syscall
    
    # Display prompt
    ori     $v0, $0, 4
    lui     $a0, 0x1001
    ori     $a0, $a0, 0x37
    syscall
    
    # Read input number
    ori     $v0, $0, 5
    syscall 
    ori     $s0, $0, 0
    addu    $s0, $v0, $s0
    
    # Display text
    ori     $v0, $0, 4
    lui     $a0, 0x1001
    ori     $a0, $a0, 0x4b
    syscall
    
    # t0 holds the count, $t1 holds temp, $t2 holds reverse
    addi    $t0, $0, 31
    
    reverseLoop:
        addi    $t1, $0, 1
        subu    $t0, $t0, $t1
        and     $t1, $s0, $t1
        xor     $t2, $t2, $t1
        sll     $t2, $t2, 1
        srl     $s0, $s0, 1
        ori     $t1, $0, 0
        bgtz    $t0, reverseLoop
    
    # Display the number
    ori     $v0, $0, 1
    add     $a0, $t2, $0
    syscall
    
    # Exit
    ori     $v0, $0, 10
    syscall
