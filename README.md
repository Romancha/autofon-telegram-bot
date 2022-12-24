# АвтоФон Telegram Бот

Self-hosted Телеграм бот для отслеживания устройств [АвтоФон](http://www.autofon.ru/).

<img src="images/bot_preview.png" width="800px">

## Функции бота

* Отслеживание ваших устойств АвтоФон
* Отправка уведомление в Телеграм с информацией о новом состоянии
* Настраиваемый список устройств для отслеживания
* Формирование графиков на основе истории состояний устройств

## Графики

Температура с датчика устройства

<img src="images/bot_chart_temp.png" width="500px">

Напряжения батареи

<img src="images/bot_chart_volt.png" width="500px">

Затраченное мАч батаерии

<img src="images/bot_chart_consumption.png" width="500px">

## Установка

Для запуска бота необходимо:

1. Установить [Docker](https://docs.docker.com/get-docker/) и [Docker Compose](https://docs.docker.com/compose/install/).
2. Создать вашего бота и получить токен у [@BotFather](https://t.me/BotFather).
3. Узнать свой `chat_id` у [@userinfobot](https://t.me/userinfobot).
4. Заполнить [docker-compose.yml](/src/main/docker/docker-compose.yml) файл: обязательными переменными ``AUTOFON_TELEGRAM_TOKEN``, ``AUTOFON_TELEGRAM_CHAT_ID``, ``AUTOFON_API_KEY``, ``AUTOFON_API_PASSWORD``.
https://github.com/Romancha/autofon-telegram-bot/blob/7d83bf06cbb0f98672e324e1af602015dbd29768/src/main/docker/docker-compose.yml#L1-L12
5. выполнить команду для запуска ``docker-compose up -d``

## Доступные параметры

| Параметр                                   | Описание                                                                                                                                                        |
|--------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| AUTOFON_TELEGRAM_TOKEN                     | Токен телеграм бота, полученный у [@BotFather](https://t.me/BotFather)                                                                                          |
| AUTOFON_TELEGRAM_CHAT_ID                   | Идентификатор чата, куда бот будет слать уведомления. Обычно это ваш чат с ботом, можно воспользовать [@userinfobot](https://t.me/userinfobot) для получения id |
| AUTOFON_API_KEY                            | Api ключ АвтоФон, узнать можно в [личном кабинете АвтоФон](https://control.autofon.ru/options/) - API Key                                                       |
| AUTOFON_API_PASSWORD                       | Пароль от вашего аккуанта АвтоФон                                                                                                                               |
| AUTOFON_TIME_ZONE                          | Временная зона [ZoneId](https://docs.oracle.com/javase/8/docs/api/java/time/ZoneId.html), по умолчанию ``Europe/Moscow``                                        |
| AUTOFON_HEARTBEAT_ENABLED                  | Включить отправку уведомлений о состоянии бота, по умолчанию ``false``                                                                                          |
| AUTOFON_HEARTBEAT_INTERVAL_SECONDS         | Интервал отправки уведомлений о состоянии бота в секундах, по умолчанию ``10800`` (3 часа)                                                                      |
| AUTOFON_CHECK_LAST_UPDATE_INTERVAL_SECONDS | Интервал проверки обновлений устройств в секундах, по умолчанию ``600`` (10 минут)                                                                              |
