# Microloan Issuance Service

![Java](https://img.shields.io/badge/Java-17%2B-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0%2B-green)
![License](https://img.shields.io/badge/License-MIT-yellow)

Микросервис для выдачи микрозаймов с поддержкой различных профилей запуска.

## 🚀 Быстрый старт

### Требования
- Java 21 или выше
- 4+ ГБ оперативной памяти
- Поддерживаемые ОС: Linux, Windows, macOS

### Запуск приложения

Выберите подходящий вариант запуска в зависимости от среды:

#### Вариант 1: Запуск с настройками по умолчанию
```bash
java -jar microloan-issuance-0.0.1-SNAPSHOT.jar -Xms2G -Xmx4G

#### Вариант 2: Запуск с настройками по умолчанию
```bash
java -jar microloan-issuance-0.0.1-SNAPSHOT.jar --spring.profiles.active=test1 -Xms=2G -Xms=4G

#### Вариант 3 Запуск с настройками по умолчанию
```bash
java -jar microloan-issuance-0.0.1-SNAPSHOT.jar --spring.profiles.active=test2 -Xms=2G -Xms=4G
