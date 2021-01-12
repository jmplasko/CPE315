# Name:  Jesus Blanco and James Plasko
# Section:  CPE 315-03
# Description: Performs a modulus operation with two integers as inputs, 
#              a number and a div, with the div being a power of 2
# Java Code:
#public static int fastmod(int num, int div){
#    //Div is guaranteed to be a power of two
#    return(num & (div-1));  
#}

.globl welcome
.globl promptNum
.globl promptDiv
.globl modText

.data

welcome:
    .asciiz " This program performs a fast mod on a num with a div of a power of 2.\n\n "

promptNum:
    .asciiz " Enter an integer (num): "

promptDiv:
    .asciiz "  Enter an integer (div): "

modText:
    .asciiz " \n  Mod = "

.text

main:
    
    # Display welcome message
    ori     $v0, $0, 4
    lui     $a0, 0x1001
    syscall
    
    # Display num prompt
    ori     $v0, $0, 4
    lui     $a0, 0x1001
    ori     $a0, $a0, 0x4a
    syscall
    
    # Read input num
    ori     $v0, $0, 5
    syscall
    ori     $s0, $0, 0
    addu    $s0, $v0, $s0
    
    # Display div prompt
    ori     $v0, $0, 4
    lui     $a0, 0x1001
    ori     $a0, $a0, 0x64
    syscall
    
    # Read input div
    ori     $v0, $0, 5
    syscall
    
    # Perform our calculation
    addiu   $t0, $t0, 1
    subu    $v0, $v0, $t0
    and     $s0, $s0, $v0
    
    # Display text
    ori     $v0, $0, 4
    lui     $a0, 0x1001
    ori     $a0, $a0, 0x81
    syscall
    
    # Display the mod
    ori     $v0, $0, 1
    add     $a0, $s0, $0
    syscall
    
    # Exit
    ori     $v0, $0, 10
    syscall
