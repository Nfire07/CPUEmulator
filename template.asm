;template.asm
LDI R1 NULL 0000
LDI R3 NULL 0003
LDI R2 NULL 0002
LDI SP NULL 1000
;push allo stack
LDIM SP R2
INC SP NULL

LDI R2 NULL 0001

;pull allo stack
LDFM R2 SP
DEC SP NULL


;ADD R1 R3
;DEC R2 NULL
;LDNZI IP NULL 0004
HALT
