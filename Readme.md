# Сервис для работы с принтерами Dreamkas

## Конфигурация

## Запросы

**POST /api/fiscal/:printerId/session_open**   

параметры  
* cashier_name - не обязательный

**GET /api/fiscal/:printerId/report_x**   

без параметров

**POST /api/fiscal/:printerId/report_z**   

* cashier_name - не обязательный


**POST /api/fiskal/1/receipt**

*json параметры*

  **tickets** список билетов
  
  описание сущности билет см ниже

  **taxMode** режим налогообложения
  
  * default - обычный 
  * simple_income - Упрощенная Доход
  * simple_income_outcome - Упрощенная Доход минус Расход 
  * temporary_income - Единый налог на вмененный доход
  * patent - Патентная
  * esn - Единый сельскохозяйственный налог
  
  
  **checkId** номер чека
  число
  
  **cashier** объект кассир
   , см ниже
   
   
  **paymentType** тип оплаты 
  
  cash | cashless
  
  
  **paymentMode** Признак способа расчета
   
   * full_prepayment - Предоплата 100%
   * prepayment - Предоплата
   * advance - Аванс
   * full_payment - Полный расчет
   * partial_payment - Частичный расчет и кредит
   * credit - Передача в кредит
   * credit_payment - Оплата кредита
   
   
  **documentType** тип чека
  
  * payment - покупка
  * refund - возврат
  
##Сущности
  
  ###Ticket - билет
  
  * showName - название сеанса
  * performanceDateTime - дата и время сеанса, формат ISO  с миллисекундами
  * price - цена, целое число два последних разряда копейки, например если цена 25р 30к передавать 2530
  * hall - зал
  * row - ряд
  * discount - объект скидка, формат см ниже. На расчет не влияет,
  используется в информативных целях 
  * place - место
  * ageLimit - возрастное ограничение
  * series - серия билета(БСО)
  * number - номер билета(БСО)
  
###Cashier кассир

* cashier_name  ФИО кассира - *обязательный*
* inn инн кассира - *необязательный*

###Discount скидка

* name - название
* amount - размер

* cashier_name  ФИО кассира - *обязательный*
* inn инн кассира - *необязательный*

```json
{
  "tickets": [
    {
      "showName": "Мстители 3D",
      "performanceDateTime": 1560632400000,
      "price": 10000,
      "hall": "Зал 1",
      "row": "Балкон 1",
      "discount": {
        "name": "Студент",
        "amount": 3000
      },
      "place": "34",
      "ageLimit": 16,
      "series": "АА",
      "number": 10
    },
    {
      "showName": "Мстители 3D",
      "performanceDateTime": 1560632400000,
      "price": 20000,
      "hall": "Зал 1",
      "row": "Балкон 1",
      "discount": {
        "name": "Студент",
        "amount": 5000
      },
      "place": "35",
      "ageLimit": 16,
      "series": "АА",
      "number": 11
    }
  ],
  "taxMode": "default",
  "checkId": 142,
  "cashier": {
    "cashier_name": "Иванов А.О."
  },
  "paymentType": "cash",
  "paymentMode": "full_prepayment",
  "documentType": "refund"
}
```


