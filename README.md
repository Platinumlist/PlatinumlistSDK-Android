# PlatinumlistSDK-Android
[![](https://jitpack.io/v/Platinumlist/PlatinumlistSDK-Android.svg)](https://jitpack.io/#Platinumlist/PlatinumlistSDK-Android)

## About

There are 3 types of work in SDK.
Select-tickets - Users can select tickets and seats on the hall map, fill in custom questions if they are set up, see the order summary page. Payment is happening on the Partner’s side.
Purchase - Users can select tickets and seats on the hall map, fill in custom questions if they are set up, see the order summary page, select Platinumlist payment gateway to pay with.
Ticket-office - Users can select tickets and seats on the hall map, fill in custom questions if they are set up, see the order summary page, select Platinumlist payment gateway to pay with, see the successful purchase pageYou can open the SDK by event id or show id. If you open the SDK by show id, then the parameter “Event show setting” will help you to hide the calendar on the select tickets page.
You can open Arabic or English version of SDK.Here is the list of actions to be performed using Platinumlist API
To extract event id, show id, event info, order details and perform other actions use API
https://docs.platinumlist.net/api/v7/#header-use-api-for-perform-partner-sale

## Setup

1. Add it to your build.gradle with:

```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
and:

```gradle
dependencies {
    implementation 'com.github.Platinumlist:PlatinumlistSDK-Android:{latest version}'
}
```

2. Add widget to your layout:

```xml
<com.platinumlist.sdk.PlatinumView
   android:id="@+id/platinumView"
   android:layout_width="match_parent"
   android:layout_height="match_parent" />
```

3. implement the PlatinumView.Adapter.
By override method orderStatusResolver(orderID : Int, status : String) you can get orderID and status of your order.

```kotlin
override fun orderStatusResolver(orderId: Int, status: String) {
    show("orderStatusResolver(orderId: $orderId, status: $status)")
}
```

- **orderId** - int,
 - **status** - string, `completed|expired|pending payment` - current order status


By override method provideUser() you can specify user data.

```kotlin
override suspend fun provideUser(): UserRequest? {
    return UserRequest(
        name = "User Userovich",
        cityId = 1,
        email = "email@gmail.com",
        nationalityId = 254,
        phone = "+971501234567"
    )
}
```
- **name** - string, 5 - 200 chars
- **email** - string, 5 - 200 chars
- **phone** - string, expect valid phone number
- **nationalityId** - int, [Method returns available nationality ids](https://docs.platinumlist.net/api/v7/#country-country-list-get)
- **cityId** - int, [Method returns available city ids](https://docs.platinumlist.net/api/v7/#city-city-list)

 `When the first ticket was added the basket has expiration time equal 15 minutes.`


This method executes in invalidate() method.

4. set adapter to view:

```kotlin
platinumView.adapter = adapter
```

5. call invalidate method

```kotlin
adapter.invalidate()
```

6. call widget clear() method for release memory

```kotlin
platinumView.clear()
```

7. By override method getSdkType() you can return sdk type.

```kotlin
override fun getSdkType(): SdkType {
	return Storage.sdkType
}

enum class SdkType(val typeName: String) {
	PURCHASE(“purchase”),
	TICKET_OFFICE(“ticket-office”),
	SELECT_TICKETS(“select-tickets”)
}
```

8. By override method provideEventShowSetting() you can return event_show_setting type.

```kotlin
override suspend fun provideEventShowSetting(): Int {
	return Storage.isEventShowSetting
}
```

9. By override method provideLanguage() you can return language for WebView (“en” | “ar”).

```kotlin
override suspend fun provideLanguage(): String {
	return Storage.lang
}
```

10. By override method triggerError(message: String, code: Int) you can get message and code of error.

```kotlin
override fun triggerError(message: String, code: Int) {
	show(“triggerError(message: $message, code: $code)”)
}
```

11. Specify in setOrderId where you want to write the ID of order.

```kotlin
override fun setOrderId(orderId: Long) {
	Storage.orderId = orderId
}
```

12. Specify in setOrderAmount where you want to write the amount of order.

```kotlin
override fun setOrderAmount(amount: Float) {
	Storage.amount = amount
}
```

13. Сreate an instance of the OrderResultView class and inject context.
Then you can call the methods of completed or failed order status.
These methods return the result of the status update:
- true - update was successful;
- false - update crashed.

```kotlin
val orderRepository = OrderResultView(context)

orderRepository.updateToCompleted(orderId, amount)
orderRepository.updateToFailed(orderId)
```
- **orderId** - long- id of current order,
- **amount** - float- amount of current order.

14. If checkOut() method was called and there is no setted user, the registerUser() method from Adapter will be called. In this method you can configure user registration.

```kotlin
override fun registerUser() {
	show(“registerUser”)
}
```

### Payment
For the testing purchase use `Payfort Test` payment gateway.
[Available credit card credentials](https://paymentservices.amazon.com/docs/EN/12.html#test-payment-card-numbers)

### Order details
[Method returns info about order](https://docs.platinumlist.net/api/v7/#order-order)