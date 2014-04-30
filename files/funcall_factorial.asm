.data
en: 	.asciiz "n = "
eol: 	.asciiz "\n"
.text
 
.globl  __start 
__start: 
	la 	$a0, en # print "n = "
	li 	$v0, 4 	#
	syscall 	#

	li $v0, 5 	# read integer
	syscall 	#
	move $a0, $v0 	# $a0 = $v0	
	
	jal fac 	# $v0 = fib(n)
	
	move $a0, $v0 	# $a0 = fib(n)	
	li $v0, 1 	# print int
	syscall 	#
	
	la $a0, eol 	# print "\n"
	li $v0, 4 	#
	syscall 	#

	li $v0, 10
	syscall
 
fac: 
	slti 	$t0, $a0, 2 # if $a0<>0, goto generic case
	beq 	$t0, $zero, gen 	# else set result $v0 = 1
	addi    $v0, $zero, 1
	jr 	$ra 		# return
gen:
	addi 	$sp, $sp, -8 	# make room for 2 registers on stack
	sw 	$ra, 0($sp) 	# save return address register $ra
	sw 	$a0, 4($sp) 	# save argument register $a0=n
	
	addi 	$a0, $a0, -1 	# $a0 = n-1
	jal fac 		# $v0 = fac(n-1)
		
	lw 	$a0, 4($sp) 	# restore $a0=n
	lw 	$ra, 0($sp) 	# restore $ra
	addi 	$sp, $sp, 8 	# multipop stack
		
	mult    $v0, $a0 	# $v0 = fac(n-1) x n
	mflo    $v0
	jr 	$ra 		# return
