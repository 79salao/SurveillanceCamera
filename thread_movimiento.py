def worker():
    global command
    while True:
        command1 = modulo_mando.teclaMando()
        if command1 != None:
            print(command1)
            command = command1