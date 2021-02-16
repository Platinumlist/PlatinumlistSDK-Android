# PlatinumlistSDK-Android
[![](https://jitpack.io/v/Platinumlist/PlatinumlistSDK-Android.svg)](https://jitpack.io/#Platinumlist/PlatinumlistSDK-Android)

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
override suspend fun provideUser(): UserRequest {
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

### Payment
For the testing purchase use `Payfort Test` payment gateway. 
[Available credit card credentials](https://paymentservices.amazon.com/docs/EN/12.html#test-payment-card-numbers)

### Order details
[Method returns info about order](https://docs.platinumlist.net/api/v7/#order-order)
