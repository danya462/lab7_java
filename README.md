# Лабораторная работа 7

TCP-клиент и TCP-сервер на Java по модели "запрос-ответ".

## Что реализовано

- `EchoServer` — сервер, который принимает строку и удаляет из нее все whitespace-символы.
- `EchoClient` — клиент для интерактивного ввода или автоматической отправки тестовых строк.
- `NetworkScanner` — дополнительная программа для поиска доступных серверов в локальной подсети.

Вариант №15:

`"Hello World"` -> `"HelloWorld"`

## Структура проекта

```text
src/
  EchoServer.java
  EchoClient.java
  NetworkScanner.java
```

## Компиляция

```powershell
mkdir out -ErrorAction SilentlyContinue
javac -encoding UTF-8 -d out src\*.java
```

## Запуск сервера

По умолчанию сервер слушает порт `12345` на всех сетевых интерфейсах:

```powershell
java -cp out EchoServer
```

С указанием порта:

```powershell
java -cp out EchoServer 12345
```

С указанием порта и адреса привязки:

```powershell
java -cp out EchoServer 12345 0.0.0.0
```

## Запуск клиента

Локальное подключение:

```powershell
java -cp out EchoClient
```

Подключение к другому компьютеру:

```powershell
java -cp out EchoClient 192.168.1.10 12345
```

Автоматическая отправка тестовых строк:

```powershell
java -cp out EchoClient localhost 12345 "Hello World" " a  b	c "
```

## Проверка через telnet или netcat

### Telnet

```powershell
telnet localhost 12345
```

После подключения введите строку и нажмите Enter.

### Netcat

```powershell
nc localhost 12345
```

## Кодировка

Во всех программах используется `UTF-8`:

- `InputStreamReader(..., StandardCharsets.UTF_8)`
- `OutputStreamWriter(..., StandardCharsets.UTF_8)`

Если в Windows-консоли отображаются некорректные символы, можно переключить кодовую страницу:

```powershell
chcp 65001
```

## Работа по сети между двумя компьютерами

1. На первом компьютере запустить `EchoServer`.
2. Узнать IP-адрес первого компьютера командой `ipconfig`.
3. На втором компьютере запустить `EchoClient`, передав IP сервера:

```powershell
java -cp out EchoClient 192.168.1.10 12345
```

Что изменилось по сравнению с `localhost`:

- сервер оставлен тем же;
- клиенту передается реальный IP-адрес сервера;
- может потребоваться разрешить входящие подключения в firewall;
- оба компьютера должны находиться в одной сети или иметь маршрут друг к другу.

## Поиск серверов в сети

Автоматическое определение локальной подсети:

```powershell
java -cp out NetworkScanner
```

С указанием подсети, порта и таймаута:

```powershell
java -cp out NetworkScanner 192.168.1. 12345 500
```

## Проверка типа "свой-чужой"

В базовой версии она не реализована. Для усложнения можно добавить:

- пароль в первом сообщении клиента;
- список разрешенных IP-адресов;
- специальную команду рукопожатия перед основной работой.
