# NewsPortal

# About
Its an android app based which will give you the news regarding Computer Science and Entrepreneurship , To read more about the news you can go its complete description in the app also.

# Technologies used
Language :- Java SE 8, XML
IDE :- Android Studio 
Api :- Hackernews API (https://newsapi.org/s/hacker-news-api)

# Technical summary
>> We firs make an SQLite database and in it we create a table with columns ID, Title and Content which corresponds to the id, title and content of the news.

>> With the help of Hackernews api we download the news in json format, for which we use the 'Asynctask' class provided in Android studio , which is basically used to get the output from a given url.

>>The output of the url consist of an array of news item, we get to them one by one and take the imporatand newsid from it and use
Asynctask again to get to that news article.

>>After getting this we store it in the SQLite database.

>>Now we use listview to take the news title and display it in series in view.

>>On clicking of any paritcular new item, it opens up a new activity through 'intent'.

>>In the new Activity all the newscontent is displayed.

>>The user can then go back and to select another news item.

