# Демо-приложение для лекций по разработке мобильных приложений

**Пулл реквесты в этот репозиторий не принимаются: он создан в демонстративных целях.**

Данное приложение по захардкоженному ключу API и  демонстрирует работу с Git и простой рефакторинг.

## Как запустить?
Можно просто склонировать репозиторий себе. Можно даже сделать это с помощью Android Studio, нажав на кнопку "New Project from Version Control" в контекстном меню или "Get from VCS" на приветственном экране.

Если проект не импортируется – возможно, репозиторий слишком долго не обновлялся и не поддерживается актуальными версиями Gradle или Android Gradle Plugin.

Также в папке app понадобится создать файл Constants.kt следующего вида:
```kotlin
package com.example.steamnetworth

internal const val API_KEY = "ВАШ_КЛЮЧ_API"
internal const val STEAM_ID = "ВАШ_ИДЕНТИФИКАТОР_STEAM"
```
О том, как получить ключ API, можно прочитать в [документации Steam для разработчиков](https://steamcommunity.com/dev).

Идентификатор Steam (SteamId64) можно скопировать из адресной строки либо поискать через сторонние-сайты-конвертеры.

## Где можно посмотреть отрефакторенную версию?
В ветке full_demo находится аналогичная версия этого приложения, но с проведёнными рефакторингами:
* Загрузка данных перенесена из Activity в ViewModel и разбита на слои.
* Экран имеет единственный источник правды и получает список регионов вместе со всеми остальными данными
* Добавлено кеширование информации о пользователе после первой загрузки на время работы приложения и тесты, проверяющие данное поведение.

## Что можно попробовать доработать самому?
Можно посмотреть на код в основной ветке и задаться вопросом: "что здесь не так?"

А можно попробовать продолжить рефакторить код в ветке full_demo:
* CountriesRepository – конкретная реализация со списком стран. А что, если мы хотим выбирать из всех стран мира и получать их откуда-то, при этом не внося изменений в код, который тянет из этого репозитория список стран?
* В больших проектах создание SteamNetWorthViewModel делегировалось бы DI-фреймворкам. Как бы выглядело создание этого класса с помощью подобных фреймворков?
* На текущий момент просто так протестировать метод SteamNetWorthViewModel.loadData нельзя: нужно прописывать дополнительные правила в тесте, чтобы заработал viewModelScope. Как можно отрефакторить загрузку таким образом, чтобы не прописывать дополнительных правил по работе с viewModelScope?
* Данные о пользователе кешируются в памяти. Однако после перезапуска приложения этот кеш будет пустым. Как нам сохранить данные о пользователе между перезапусками?