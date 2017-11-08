; assemble this file into base.out with "nasm -f bin -o base.out base.asm"

BITS 32
    org         08048000h
    ehdr:                                               ; Elf32_Ehdr
                db      7Fh, "ELF", 1, 1, 1, 0         ;   e_ident
        times 8 db      0
                dw      2                               ;   e_type
                dw      3                               ;   e_machine
                dd      1                               ;   e_version
                dd      _start                          ;   e_entry
                dd      phdr - $$                       ;   e_phoff
                dd      0                               ;   e_shoff
                dd      0                               ;   e_flags
                dw      ehdrsize                        ;   e_ehsize
                dw      phdrsize                        ;   e_phentsize
                dw      1                               ;   e_phnum
                dw      0                               ;   e_shentsize
                dw      0                               ;   e_shnum
                dw      0                               ;   e_shstrndx

    ehdrsize    equ     $ - ehdr

    phdr:                                               ; Elf32_Phdr
                dd      1                               ;   p_type
                dd      0                               ;   p_offset
                dd      $$                              ;   p_vaddr
                dd      $$                              ;   p_paddr
                dd      filesize                        ;   p_filesz
                dd      filesize                        ;   p_memsz
                dd      5                               ;   p_flags
                dd      1000h                           ;   p_align

    phdrsize    equ     $ - phdr

    stdin       equ     0
    stdout      equ     1
    ICANON      equ     2
    ECHO        equ     8
    data        equ     36
    c_lflag     equ     12

    _start:
            mov eax, 192    ; mmap2
            xor ebx, ebx    ; addr
            mov ecx, 30000  ; len
            mov edx, 6h     ; prot = PROT_READ|PROT_WRITE
            mov esi, 22h    ; flags = MAP_PRIVATE|MAP_ANONYMOUS
            mov edi, 0      ; fd
            xor ebp, ebp    ; offset
            int 80h
            mov edi, eax    ; edi = ptr to memory

            call read_termios
            and dword [edi+c_lflag], ~(ICANON|ECHO)
            call write_termios

            lea esi, [edi+data]
            call run

            or dword [edi+c_lflag], ICANON|ECHO
            call write_termios

            mov eax,1       ; exit
            int 80h

        read_termios:
            mov eax, 36h
            mov ebx, stdin
            mov ecx, 5401h
            mov edx, edi
            int 80h
            ret

        write_termios:
            mov eax, 36h
            mov ebx, stdin
            mov ecx, 5402h
            mov edx, edi
            int 80h
            ret

        read_char:
            mov eax, 3     ; sys_read
            mov edx, 1     ; length
            mov ecx, esi   ; buffer
            mov ebx, stdin
            int 80h
            ret

        write_char:
            mov eax, 4      ; sys_write
            mov edx, 1
            mov ecx, esi
            mov ebx, stdout
            int 80h
            ret

    run:
        ;ret

        ;dec esi
        ;nop
        ;inc esi
        ;nop
        ;dec byte [esi]
        ;nop
        ;inc byte [esi]
        ;dec esi
        ;comp:
        ;cmp byte[esi],0
        ;je near next
        ;inc esi
        ;jmp near comp
        ;next:
        ;dec esi

        ;call write_char
        ;call read_char
        ;call write_char
        ;call read_char
        ;ret

    filesize    equ     $ - $$
