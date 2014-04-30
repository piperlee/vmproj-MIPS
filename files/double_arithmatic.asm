##
## doublemultiples.asm - takes two doubles A and B, and prints out
##      all the multiples of A from A to A * B.
##      If B <= 0, then no multiples are printed. 
## Registers used:
##      $f0 & $f1 - used to hold A.
##      $f2 & $f3- used to hold B.
##      $f4 - used to store S, the sentinel value A * B.
##      $f6 - used to store m, the current multiple of A.

        .text
main:
        ## read A into $t0, B into $t1. 
        la      $a0, typeina
        li      $v0, 4
        syscall
        li      $v0, 7               # syscall 7 = read_double
        syscall
        l.d    $f0, $f12             # A = double in $f0 and $f1

	la      $a0, typeinb
	li      $v0, 4
	syscall
        li      $v0, 7               # syscall 7
        syscall
        l.d    $f2, $f12             # B = double in $f2 and $f3

        mul.d   $f4, $f0, $f2        # S = A * B.
        l.d    $f6, $f4             # m = A

        la      $a0, printmul
        li      $v0, 4
        syscall
        l.d    $f12, $f6             # print m.
        li      $v0, 3               # syscall 3 = print_double    
        syscall                      # make the syscall

        li      $v0, 10              # syscall 10 = exit
        syscall                      # we're out here

## Here's where the data for this program is stroed:
        .data
space:          .asciiz " "
newline:        .asciiz "\nAll Multiplies Are Printed."
printmul: .asciiz "Here's a multiply:"
typeina: .asciiz "Pls Type in the First double:"
typeinb: .asciiz "Pls Type in the Second double:"

## end of the doublemultiples.asm
