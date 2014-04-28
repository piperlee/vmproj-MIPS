fact:
 
	slti $t0, $a0, 2	# if i < 2 (i.e i == 1)
	beq $t0, $zero, main	# if i >= 2 go to main
	addi $v0, $zero, 1	# else make the resturn value 1
 
	jr $ra
 
 
main:
 
	# OPERATION 1: save into stack
 
	addi $sp, $sp, -8	# make space in the stack
	sw $ra, 0($sp)		# save the return address
	sw $a0, 4($sp)		# save the argument value
 
	# OPERATION 2: compute fact(n - 1)
 
	addi $a0, $a0, -1
	jal fact
 
	# OPERATION 3: restore from stack
 
	lw $ra, 0($sp)		# get old return address from stack
	lw $a0, 4($sp)		# get old argument value from stack
	addi $sp, $sp, 8	# return stack pointer to original value, thus erasing all values
 
	# OPERATION 4: finally n * fact(n - 1)
 
	mult $v0, $a0		# multiply n * fib(n - 1)
	mflo $v0		# gets the result of the multiplication from the low register
 
	jr $ra