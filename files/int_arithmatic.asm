##
## multiples.asm - takes two numbers A and B, and prints out
##      all the multiples of A from A to A * B.
##      If B <= 0, then no multiples are printed. 
## Registers used:
##      $t0 - used to hold A.
##      $t1 - used to hold B.
##      $t2 - used to store S, the sentinel value A * B.
##      $t3 - used to store m, the current multiple of A.

        .text
	.global __start
__start:
        ## read A into $t0, B into $t1. 
        la      $a0, typeina
        li      $v0, 4
        syscall
        li      $v0, 5               # syscall 5 = read_int 
        syscall
        move    $t0, $v0             # A = integer just read

		la      $a0, typeinb
		li      $v0, 4
		syscall
        li      $v0, 5               # syscall 5
        syscall
        move    $t1, $v0             # B = integer just read

        blez    $t1, exit            # if B <= 0, exit.

        mul     $t2, $t0, $t1        # S = A * B.
        move    $t3, $t0             # m = A

loop:
		la      $a0, printmul
		li      $v0, 4
		syscall
        move    $a0, $t3             # print m.
        li      $v0, 1               # syscall 1 = print_int    
        syscall                      # make the syscall

        beq     $t2, $t3, endloop    # if m == S, we're done.
        add     $t3, $t3, $t0        # otherwise, m = m + A

        la      $a0, space           # print a space
        li      $v0, 4               # syscall 4 = print_string
        syscall

        b       loop
endloop:
        la      $a0, newline         # print a newline
        li      $v0, 4               # syscall 4
        syscall

exit:
        li      $v0, 10              # syscall 10 = exit
        syscall                      # we're out here

## Here's where the data for this program is stroed:
        .data
space:          .asciiz " "
newline:        .asciiz "\nAll Multiplies Are Printed."
printmul: .asciiz "\nHere's a multiply:"
typeina: .asciiz "Pls Type in the First Int:"
typeinb: .asciiz "Pls Type in the Second Int:"

## end of the multiples.asm
