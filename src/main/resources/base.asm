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

    wc          dd      write_char
    rc          dd      read_char

    _start:
            ; reserve memory
            mov eax, 192    ; mmap2
            xor ebx, ebx    ; addr
            mov ecx, 30000  ; len
            mov edx, 6h     ; prot = PROT_READ|PROT_WRITE
            mov esi, 22h    ; flags = MAP_PRIVATE|MAP_ANONYMOUS
            xor edi, edi    ; fd
            xor ebp, ebp    ; offset
            int 80h
            mov ebp, eax    ; ebp = ptr to memory

            ; disable ICANON and ECHO on terminal
            mov ecx, 5401h  ; read termios
            call termios
            and dword [ebp+c_lflag], ~(ICANON|ECHO)
            inc ecx         ; write termios
            call termios

            lea ecx, [ebp+data] ; set ecx as bf pointer
            mov esi, rc     ; enable indirect calls (shorter opcode)
            mov edi, wc
            mov edx, 1      ; length for read/write calls

            call run

            ; reset terminal ICANON and ECHO
            or dword [ebp+c_lflag], ICANON|ECHO
            mov ecx, 5402h  ; write termios
            call termios

            ; goodbye
            mov eax, 1      ; exit
            int 80h

        termios:
            mov eax, 36h
            mov ebx, stdin
            mov edx, ebp
            int 80h
            ret

        read_char:
            mov eax, 3     ; sys_read
            mov ebx, stdin
            ; ecx = buffer is already set
            int 80h
            ret

        write_char:
            mov eax, 4      ; sys_write
            mov ebx, stdout
            ; ecx = buffer is already set
            int 80h
            ret

    run:

    filesize    equ     $ - $$
