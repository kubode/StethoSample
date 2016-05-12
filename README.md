[Stetho](https://facebook.github.io/stetho/)

```javascript
importPackage(android.widget)
importPackage(android.os)
var handler = new Handler(Looper.getMainLooper());
handler.post(function() { Toast.makeText(context, "hello", Toast.LENGTH_LONG).show() });
```
