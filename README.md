SectionProgressBar project
===============

一个显示等级进度的控件,可以自定义等级值、颜色、游标等。

<img src="./preview/section.gif">

Usage
-----
```java
        mSectionBar = (SectionProgressBar) findViewById(R.id.section_2);
        mSectionBar.setLevels(mLevels);
        mSectionBar.setLevelValues(mLevelValues);
        mSectionBar2.setCurrent(3000);
```

```xml
<cc.solart.sectionbar.SectionProgressBar
        android:id="@+id/section_progress_bar"
        android:layout_width="match_parent"
        android:layout_height="70dp"
        app:section_bar_cursor="@drawable/icon_arrow_state_blue"
        app:section_foreground="@color/progress" />
```
    
License
-------

    Copyright 2016 solartisan/imilk

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.