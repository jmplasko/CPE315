# Name:  Jesus Blanco and James Plasko
# Section:  CPE 315-03
# Description: Divides a 64-bit unsigned number with a 31-bit unsigned number
# Java code:
#public static int divide(int divHigh, int divLow, int divisor){
#    if (divisor == 0){System.out.print("Cannot divide by zero.\n");}
#    if (divisor == 1){System.out.print("Quotient High: " + divHigh + "\nQuotient Low: " + divLow);}
#
#    int holder = divisor;
#    int rightShifts = 32;
#    boolean loop = True;
#    int lefShifts = 0;
#    int temp = 0;
#
#    while (loop){
#         leftShifts++;
#         temp = 1;
#         temp = temp & holder;
#         holder = holder >> 1;
#         if (temp == 1){
#            loop = False;
#            break;
#         }
#    }
#
#    leftshifts = leftshifts - 1;
#
#    holder = divHigh;
#    rightShifts = rightShifts - leftShifts;
#    holder = holder << rightShifts;
#
#    divHigh = divHigh >> leftShifts;
#    divLow = divLow >> leftShifts;
#    divLow = divLow ^ holder;
#
#    System.out.print("Quotient High: " + divHigh + "\nQuotient Low: " + divLow);
#
#    return 1;
#}

.globl welcome
.globl divHighPrompt
.globl divLowPrompt
.globl divisorPrompt
.globl quotientHighText
.globl quotientLowText

.data

welcome:
    .asciiz " This program divides a 64-bit unsigned number with a 31-bit unsigned number.\n\n"

divHighPrompt:
    .asciiz "  Enter the divident high (integer): "
    
divLowPrompt:
    .asciiz " Enter the divident low (integer): "

divisorPrompt:
    .asciiz " Enter a divisor (integer): "

quotientHighText:
    .asciiz " \n Quotient high = "
    
quotientLowText:
    .asciiz " \n Quotient high = "

divZero:
    .asciiz " \n Cannot divide by zero. "
.text

main:
    # Display welcome message
    ori     $v0, $0, 4
    lui     $a0, 0x1001
    syscall
    
    # Display upper divident prompt
    ori     $v0, $0, 4
    lui     $a0, 0x1001
    ori     $a0, $a0, 0x51
    syscall
    
    # Upper Divident input goes to $s0
    ori     $v0, $0, 5
    syscall
    ori     $s0, $0, 0
    addu    $s0, $v0, $s0
    
    # Display lower divident prompt
    ori     $v0, $0, 4
    lui     $a0, 0x1001
    ori     $a0, $a0, 0x76
    syscall
    
    # Lower Divident input goes to $s1
    ori     $v0, $0, 5
    syscall
    ori     $s1, $0, 0
    addu    $s1, $v0, $s1
    
    # Display divisor prompt
    ori     $v0, $0, 4
    lui     $a0, 0x1001
    ori     $a0, $a0, 0x9a
    syscall
    
    # Divisor input goes to $s2
    ori     $v0, $0, 5
    syscall
    ori     $s2, $0, 0
    addu    $s2, $v0, $s2
    
    # s3 will hold a permanent value of one.
    addiu   $s3, $0, 1
    
    # BRANCH IF 0
    beq     $s2, $0, divByZero
    # BRANCH IF 1
    beq     $s2, $s3, display
    
    # Copy over divisor
    addu    $t3, $0, $s2
    # s4 will be the number of right shifts necessary
    addiu   $s4, $0, 32
    #----------------------------------------------------------------------
    #Find how many shifts it takes
    shiftloop:
        # s5 holds number of shifts
        addiu    $s5, $s5, 1
        #t1 is used to determine when done
        addi    $t1, $0, 1
        and     $t1, $t1, $t3
        srl     $t3, $t3, 1
        #t1 is one, then we have found number of shifts, otherwise keep looping
        bne     $t1, $s3, shiftloop
    subu    $s5, $s5, $s3
    
    # Clear t3 then copy over upper divident to it
    ori     $t3, $0, 0
    addu    $t3, $0, $s0
    # Find number of shifts necessary
    subu    $s4, $s4, $s5
    # Shift t3 left by the amount of shifts
    sllv    $t3, $t3, $s4
    
    # Shift inputs
    srlv     $s0, $s0, $s5
    srlv     $s1, $s1, $s5
    
    xor      $s1, $s1, $t3
    
    display:
        # Display upper quotient text
        ori     $v0, $0, 4
        lui     $a0, 0x1001
        ori     $a0, $a0, 0xb7
        syscall
    
        ori     $v0, $0, 1
        add     $a0, $s0, $0
        syscall
        
        # Display lower quotient text
        ori     $v0, $0, 4
        lui     $a0, 0x1001
        ori     $a0, $a0, 0xcb
        syscall
        
        ori     $v0, $0, 1
        add     $a0, $s1, $0
        syscall

    
    endProg:
        # Exit
        ori     $v0, $0, 10
        syscall
    
    divByZero:
        ori     $v0, $0, 4
        lui     $a0, 0x1001
        ori     $a0, $a0, 0xdf
        syscall
        beq     $0, $0, endProg
