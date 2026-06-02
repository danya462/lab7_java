# Лаба 7 - TCP echo-сервер

Вариант 15: сервер принимает строку и возвращает ее без whitespace-символов.

Пример:

```text
Hello World -> HelloWorld
 a  b	c  -> abc
```

## Файлы

- `src/EchoServer.java` - TCP-сервер;
- `src/EchoClient.java` - TCP-клиент;
- `src/NetworkScanner.java` - поиск сервера в локальной сети.

## Запуск

Сервер:

```powershell
mvn exec:java
```

Клиент в другом окне:

```powershell
mvn exec:java -Dexec.mainClass=EchoClient
```

Автоматическая проверка:

```powershell
mvn exec:java -Dexec.mainClass=EchoClient -Dexec.args="localhost 12345 Hello World"
```
