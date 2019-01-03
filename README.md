Fair Split App Code
============

Forked from the Google android-vision repo.  I modified the visionSamples/ocr-reader project to be an application called Fair Split that reads the prices off of a restaurant receipt and lets you distribute items between a list of payers.

Features:
- App autodetects list of prices if entire receipt fits in single camera view
- Manually correct misread prices or add missing prices
- Select payers from contact list, or type in names
- Split items evenly between multiple payers
- Automatically calculate variable tip percentage
- Text invoice to payers

Pre-requisites
--------------
 Android Play Services SDK level 26 or greater.

Getting Started
---------------
Builds using Gradle in Android Studio.  There is no special
configuration required.

Privacy Policy
--------------
See Privacy Policy [here](Privacy.md)

License
-------

Copyright 2019 DW Wheeler. All Rights Reserved.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.
