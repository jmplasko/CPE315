# Name:  Jesus Blanco and James Plasko
# Section:  CPE 315-03
# Description: Draws a stick figure.
# Circle ( 30, 100, 20) head
    addi    $a0, $0, 30
    addi    $a1, $0, 100
    addi    $a2, $0, 20
    jal Circle

# Line(30,80,30,30) body
    addi    $a0, $0, 30
    addi    $a1, $0, 80
    addi    $a2, $0, 30
    addi    $a3, $0, 30
    jal Line

# Line(20,1,30,30) left leg
    addi    $a0, $0, 20
    addi    $a1, $0, 1
    addi    $a2, $0, 30
    addi    $a3, $0, 30
    jal Line

# Line(40,1,30,30) right leg
    addi    $a0, $0, 40
    addi    $a1, $0, 1
    addi    $a2, $0, 30
    addi    $a3, $0, 30
    jal Line

# Line(15,60,30,50) left arm
    addi    $a0, $0, 15
    addi    $a1, $0, 60
    addi    $a2, $0, 30
    addi    $a3, $0, 50
    jal Line

# Line(30,50,45,60) right arm
    addi    $a0, $0, 30
    addi    $a1, $0, 50
    addi    $a2, $0, 45
    addi    $a3, $0, 60
    jal Line

# Circle (24, 105, 3) left eye
    addi    $a0, $0, 24
    addi    $a1, $0, 105
    addi    $a2, $0, 3
    jal Circle

# Circle ( 36, 105, 3) right eye
    addi    $a0, $0, 36
    addi    $a1, $0, 105
    addi    $a2, $0, 3
    jal Circle

# Line(25,90,35,90) mouth center
    addi    $a0, $0, 25
    addi    $a1, $0, 90
    addi    $a2, $0, 35
    addi    $a3, $0, 90
    jal Line

# Line(25,90,20,95) mouth left
    addi    $a0, $0, 25
    addi    $a1, $0, 90
    addi    $a2, $0, 20
    addi    $a3, $0, 95
    jal Line
# Line(35,90,40,95) mouth right
    addi    $a0, $0, 35
    addi    $a1, $0, 90
    addi    $a2, $0, 40
    addi    $a3, $0, 95
    jal Line

    j endProgram

Line:

    sub     $t0, $a3, $a1   # y1-y0
    slt     $t1, $t0, $0    # Check if value is negative
    bne     $t1, $0, getAbsoluteY

    foundAbsoluteY:
    sub     $t1, $a2, $a0   # x1-x0
    slt     $t2, $t1, $0    # Check if value is negative
    bne     $t2, $0, getAbsoluteX

    foundAbsoluteX:
    # if |y1-y0| > |x1-x0|
    slt     $t3, $t1, $t0   # Set st
    bne     $t3, $0, stIsOne

    stContinue:
    # if x1 < x0
    slt     $t4, $a2, $a0   # If x1 < x0, t4 is 1
    bne     $t4, $0, secondSwap

    sub     $t4, $a2, $a0   # deltaX = x1-x0 # SO FAR SO GOOD ------------------------------------------

    sub     $t5, $a3, $a1   # deltaY = y1-y0
    slt     $t6, $t5, $0    # Check if value is negative
    bne     $t6, $0, deltaY # Get Absolute value if it is

    deltaYFound:
    add     $t7, $0, $a1    # y = y0
    slt     $t1, $a1, $a3   # Check if y0 is less than y1
    bne     $t1, $0, smY0   # Branch is y0 is smaller
    addi    $t0, $0, -1     # ystep = -1

    yStepFound:
    add     $t1, $0, $a0    # Set X to x0
    addi    $t2, $a2, 1     # Inclusive conditional

    LineLoop:
        beq $t1, $t2, complete   # End loop
        bne $t3, $0, plotYX     # Branch on st == 1

        sw      $t1, 0($sp)     # Plot X coordinate
        sw      $t7, 1($sp)     # Plot Y coordinate
        addi    $sp, $sp, 2     # Increment stack

        plotted:
        add     $t6, $t6, $t5   # error = error + deltaY
        add     $t8, $0, $t6    # t8 = error
        sll     $t8, $t8, 1     # t8 = error*2

        slt     $t9, $t8, $t4   # 0 if 2*error > deltaX
        beq     $t9, $0, incY   # IncrementY
        beq     $t8, $t4, incY  # IncrementY if equal

        continueLoop:
        addi    $t1, $t1, 1
        j LineLoop


    stIsOne:
        # Swap x0, y0
        add     $t4, $0, $a0    # temp = x0
        add     $a0, $0, $a1    # x0 = y0
        add     $a1, $0, $t4    # y0 = temp, swap complete
        # Swap x1, y1
        add     $t4, $0, $a2    # temp = x1
        add     $a2, $0, $a3    # x1 = y1
        add     $a3, $0, $t4    # y1 = temp, swap complete
        add     $t4, $0, $0     # Clear t4
        j stContinue

    secondSwap:
        # Swap x0, x1
        add     $t4, $0, $a0    # temp = x0
        add     $a0, $0, $a2    # x0 = x1
        add     $a2, $0, $t4    # x1 = temp, swap complete
        # Swap y0, y1
        add     $t4, $0, $a1    # temp = y0
        add     $a1, $0, $a3    # y0 = y1
        add     $a3, $0, $t4    # y1 = temp, swap complete
        add     $t4, $0, $0     # Clear t4
        j stContinue

    smY0:
        addi   $t1, $0, 0       # Clear t1
        addi   $t0, $0, 1       # ystep = 1
        j yStepFound

    plotYX:
        sw      $t7, 0($sp)     # Plot X coordinate
        sw      $t1, 1($sp)     # Plot Y coordinate
        addi    $sp, $sp, 2     # Increment stack
        j plotted

    incY:
        add    $t7, $t7, $t0    # y = y + yStep
        sub    $t6, $t6, $t4    # error = error - deltaX
        j continueLoop

    deltaY:
        addi   $t6, $0, 0       # Clear t6
        sub    $t5, $a1, $a3    # |y0-y1|
        j deltaYFound

    getAbsoluteY:
        addi   $t1, $0, 0       # Clear t1
        sub    $t0, $a1, $a3    # y0-y1
        j foundAbsoluteY

    getAbsoluteX:
        addi   $t2, $0, 0       # Clear t2
        sub    $t1, $a0, $a2    # x0-x1
        j foundAbsoluteX

    complete:
       addi    $t0, $0, 0
       addi    $t1, $0, 0
       addi    $t2, $0, 0
       addi    $t3, $0, 0
       addi    $t4, $0, 0
       addi    $t5, $0, 0
       addi    $t6, $0, 0
       addi    $t7, $0, 0
       addi    $t8, $0, 0
       addi    $t9, $0, 0
       jr   $ra

Circle:
    #a0 = xc, a1 = yc, a2 = r
    #t0 = x, t1 = y, t2 = g, t3 = diagonalInc, t4 = rightInc, t5 = 2*r, 4*r, slt checker

    addi    $t0, $0, 0      #   x = 0
    add     $t1, $0, $a2    #   y = r
    # g = 3 - 2*r
    addi    $t2, $0, 3      # g = 3
    add     $t5, $0, $a2    # t5 = r
    sll     $t5, $t5, 1     # t5 = r*2
    sub     $t2, $t2, $t5   # g = 3 - 2*r

    # diagonalInc = 10 - 4*r
    sll     $t5, $t5, 1     # t5 = 4*r
    addi    $t3, $0, 10     # diagonalInc = 10
    sub     $t3, $t3, $t5   # diagonalInc = 10-4*r

    # rightInc = 6
    addi    $t4, $0, 6

    addi    $t9, $t1, 1     # Conditional purposes ( x <= y)
    # while x < = y
    whileLoop:
        beq     $t0, $t9, exitLoop

        # plot (xc+x, yc+y)
        add     $t6, $a0, $t0   # xc + x
        add     $t7, $a1, $t1   # yc + y
        sw      $t6, 0($sp)     # Plot X coordinate
        sw      $t7, 1($sp)     # Plot Y coordinate
        addi    $sp, $sp, 2     # Increment stack

        # plot (xc+x, yc-y)
        sub     $t7, $a1, $t1   # yc - y
        sw      $t6, 0($sp)     # Plot X coordinate
        sw      $t7, 1($sp)     # Plot Y coordinate
        addi    $sp, $sp, 2     # Increment stack

        # plot (xc-x, yc+y)
        sub     $t6, $a0, $t0   # xc - x
        add     $t7, $a1, $t1   # yc + y
        sw      $t6, 0($sp)     # Plot X coordinate
        sw      $t7, 1($sp)     # Plot Y coordinate
        addi    $sp, $sp, 2     # Increment stack

        # plot (xc-x, yc-y)
        sub     $t7, $a1, $t1   # yc - y
        sw      $t6, 0($sp)     # Plot X coordinate
        sw      $t7, 1($sp)     # Plot Y coordinate
        addi    $sp, $sp, 2     # Increment stack

        #  plot (xc+y, yc+x)
        add     $t6, $a0, $t1   # xc + y
        add     $t7, $a1, $t0   # yc + x
        sw      $t6, 0($sp)     # Plot X coordinate
        sw      $t7, 1($sp)     # Plot Y coordinate
        addi    $sp, $sp, 2     # Increment stack

        # plot (xc+y, yc-x)
        sub     $t7, $a1, $t0   # yc - x
        sw      $t6, 0($sp)     # Plot X coordinate
        sw      $t7, 1($sp)     # Plot Y coordinate
        addi    $sp, $sp, 2     # Increment stack

        # plot (xc-y, yc+x)
        sub     $t6, $a0, $t1   # xc - y
        add     $t7, $a1, $t0   # yc + x
        sw      $t6, 0($sp)     # Plot X coordinate
        sw      $t7, 1($sp)     # Plot Y coordinate
        addi    $sp, $sp, 2     # Increment stack

        # plot (xc-y, yc-x)
        sub     $t7, $a1, $t0   # yc - x
        sw      $t6, 0($sp)     # Plot X coordinate
        sw      $t7, 1($sp)     # Plot Y coordinate
        addi    $sp, $sp, 2     # Increment stack

        # checks g >= 0
        slt     $t5, $t2, $0        # Checks condition
        bne     $t5, $0, elseCase   # Branches on unfulfillment

        add     $t2, $t2, $t3       # g = g + diagonalInc
        addi    $t3, $t3, 8         # diagonalInc = diagonalInc + 8
        addi    $t1, $t1, -1        # y = y - 1
        addi    $t9, $t1, 2         # Conditional purposes ( x <= y)

        j continueCircle

        elseCase:
            add     $t2, $t2, $t4   # g = g + rightInc
            addi    $t3, $t3, 4     # diagonalInc = diagonalInc + 4

        continueCircle:
            addi    $t4, $t4, 4     # rightInc = rightInc + 4
            addi    $t0, $t0, 1     # x = x + 1

        j whileLoop

        exitLoop:
           addi    $t0, $0, 0
           addi    $t1, $0, 0
           addi    $t2, $0, 0
           addi    $t3, $0, 0
           addi    $t4, $0, 0
           addi    $t5, $0, 0
           addi    $t6, $0, 0
           addi    $t7, $0, 0
           addi    $t9, $0, 0
           jr   $ra

endProgram:
