##
## inputprint.asm
##      -- Input single and double,
##         print out the single and double. 
##         

main:        
        # Input the single and print
        la      $a0, insingle
        li      $v0, 4
        syscall
        li      $v0, 6 # read into $f12
        syscall
        la      $a0, outsingle
        li      $v0, 4
        syscall
        li      $v0, 2
        syscall
        la      $a0, newline
        li      $v0, 4
        syscall
        
        # Input the double and print
        la      $a0, indouble
        li      $v0, 4
        syscall
        li      $v0, 7
        syscall
        la      $a0, outdouble
        li      $v0, 4
        syscall
        li      $v0, 3
        syscall
        la      $a0, newline
        li      $v0, 4
        syscall
        
        # Exit the program
        la      $a0, endprogram
        li      $v0, 4
        syscall
        li      $v0, 10         #  syscall code  10  is for exit.
        syscall                 #  make the syscall.
        

## Data for the program
.data 
insingle: .asciiz "Pls Type in a Single:"
outsingle: .asciiz "The Single You Typed is:"
indouble: .asciiz "Pls Type in a Double:"
outdouble: .asciiz "The Double You Typed is:"
endprogram: .asciiz "Program ended."
newline: .asciiz "\n"

##  end  of  inputprint.asm
