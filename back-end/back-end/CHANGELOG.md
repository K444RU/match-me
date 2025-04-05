# Changelog

## [0.2.1](https://github.com/K444RU/match-me/compare/back-end-0.2.0...back-end-0.2.1) (2025-04-05)


### Bug Fixes

* **AuthControllerTest:** update error message for empty phone number ([d834396](https://github.com/K444RU/match-me/commit/d834396d5964b12752a3befe174a98949d40e7bc))
* **ConnectionServiceTest:** add missing mock dependency UserScoreService ([e64ec43](https://github.com/K444RU/match-me/commit/e64ec43e583cc3cf83a80fbef3530b2b7b1067ba))
* **PhoneNumberValidator:** let the @NotBlank annotation handle empty strings ([e62cac4](https://github.com/K444RU/match-me/commit/e62cac400a9686ac2067964ba3e7c8a7d134bbbd))

## [0.2.0](https://github.com/K444RU/match-me/compare/back-end-0.1.0...back-end-0.2.0) (2025-03-24)


### Features

* **ConnectionController:** add a new controller with recommendations endpoint ([6d97b3c](https://github.com/K444RU/match-me/commit/6d97b3c1dd4a4597115549dbdc0f33b3326b8321))
* **GeohashService:** implement proximity feature required for MatchingService ([e7b6653](https://github.com/K444RU/match-me/commit/e7b665324b1250428ca1ea0376389a0e9707959f))
* **GeohashService:** implement proximity feature required for MatchingService ([e7d8f63](https://github.com/K444RU/match-me/commit/e7d8f633e6f36a7e4caed4066bdb224ffcd311fa))
* **MatchingRecommendationsDTO:** add a new DTO that returns matching recommendations with info ([64f65ad](https://github.com/K444RU/match-me/commit/64f65ad17ae9a2d8d59915ceb6fee451e1059a0f))
* **MatchingRepository:** add MatchingRepository layer for DatingPool queries ([5765a42](https://github.com/K444RU/match-me/commit/5765a429e785f61e3d0d4263de9776981a5bc97e))
* **MatchingRepository:** add MatchingRepository layer for DatingPool queries ([38ca5cf](https://github.com/K444RU/match-me/commit/38ca5cfa25d72bfb6583d96d9e16987a8b21ea02))
* **PotentialMatchesNotFoundException:** add a new specific exception ([8bc4c0f](https://github.com/K444RU/match-me/commit/8bc4c0f19f5e4312df50b2159f182b23d325602d))
* **PotentialMatchesNotFoundException:** add a new specific exception ([47e8020](https://github.com/K444RU/match-me/commit/47e8020aa3b0c56d7376b5b77e1aa04179015d58))
* **UserAttributesListener:** separate userattributeslistener from datingpoolsynclistener ([3debc60](https://github.com/K444RU/match-me/commit/3debc60bc5e0eec3e83ca976a213ee082eb7e9f4))
* **UserPreferencesListener:** separate userpreferenceslistener from datingpoolsynclistener ([6c3b265](https://github.com/K444RU/match-me/commit/6c3b265f46d7bf7c05cf1a534af47890bd915dc3))
* **UserProfileListener:** separate userprofilelistener from datingpoolsynclistener ([f6cd1fb](https://github.com/K444RU/match-me/commit/f6cd1fba400e97f77b27f958d212072000cb9417))
* **UserScoreListener:** separate userscorelistener from datingpoolsynclistener ([605abbb](https://github.com/K444RU/match-me/commit/605abbbfad8b84caced1de74794ba37edf764c64))


### Bug Fixes

* **DatingPool:** change error message to correct word ([dae1eef](https://github.com/K444RU/match-me/commit/dae1eefedef0cad906fdc3c71f0e202a4fb81e4b))
* **DatingPool:** change error message to correct word ([f4e2dd1](https://github.com/K444RU/match-me/commit/f4e2dd1de8302b37288d97fb33e02bf76713cb51))
* **DatingPool:** completed denormalizing by unconnecting UserProfile ([1547646](https://github.com/K444RU/match-me/commit/1547646bcfc5789b41f57f4e0aafbd23de4e0853))
* **DatingPool:** completed denormalizing by unconnecting UserProfile ([f1988a7](https://github.com/K444RU/match-me/commit/f1988a756f47a624b99a5e6b97d49ccc5fede616))
* **DatingPool:** replace geohash regexp with correct one ([278ca52](https://github.com/K444RU/match-me/commit/278ca52ec5f39bee19b0d44457a6caee32437e1f))
* **DatingPool:** replace geohash regexp with correct one ([9f38eec](https://github.com/K444RU/match-me/commit/9f38eec2d134ce9745de2c1460fdbcd3547e8e62))
* **GeohashService:** reverse long/lat order ([1c40b45](https://github.com/K444RU/match-me/commit/1c40b457b5099571343deb3f34a66ccd40790aba))
* **GeohashService:** reverse long/lat order ([2038b3f](https://github.com/K444RU/match-me/commit/2038b3fbb7b0b708314d8e2ce863a672193179f6))
* **MatchingRecommendationsDTO:** use NotNull, probably never going to be null ([02523ba](https://github.com/K444RU/match-me/commit/02523bae7a823ad0ba4b9a8f8600d10a226e5f0e))
* **MatchingRepository:** correct query variable ([f07e82b](https://github.com/K444RU/match-me/commit/f07e82b1d163d74ea4f9c51d0a22344bb97210b6))
* **MatchingRepository:** correct query variable ([7bdee1c](https://github.com/K444RU/match-me/commit/7bdee1c296aa126a2310227e514401db9c704f7a))
* **MatchingRepository:** correct query variables ([60fbc3a](https://github.com/K444RU/match-me/commit/60fbc3aba1c9f962087e5a99728784645de9833f))
* **MatchingRepository:** correct query variables ([0bc3c01](https://github.com/K444RU/match-me/commit/0bc3c014f8331087ea4ba8f87c038933720b8020))
* **MatchingService:** add explicit casting to double ([1dc6c79](https://github.com/K444RU/match-me/commit/1dc6c79af6dcdc7237903e7007aa73e7fe3dbb3c))
* **MatchingService:** add explicit casting to double ([d83c5a0](https://github.com/K444RU/match-me/commit/d83c5a0006053df29b17f96b0832400cd3e9b354))
* **MatchingService:** fix MAXIMUM_PROBABILITY filter, was too low before ([9582688](https://github.com/K444RU/match-me/commit/958268811d65bf12df57f2547c1a35344bf37a61))
* **MatchingService:** fix MAXIMUM_PROBABILITY filter, was too low before ([281a0b4](https://github.com/K444RU/match-me/commit/281a0b4dd9428125a708ff9f344166153584f769))
* **MatchingService:** redefine ELO_K_FACTOR as SCALING_FACTOR for clarity, also as double ([18880fd](https://github.com/K444RU/match-me/commit/18880fddc0b9b6af9c4c0d0f32a90105a5a02b6e))
* **MatchingService:** redefine ELO_K_FACTOR as SCALING_FACTOR for clarity, also as double ([d1a154d](https://github.com/K444RU/match-me/commit/d1a154d08b828055b75d7c8b6b51b0f7790df27e))
* **service:** remove redundant userMessageRepository save in ChatService ([5ef7c37](https://github.com/K444RU/match-me/commit/5ef7c378b32aa8b695cf260e096538d8d4905519))
* **service:** remove redundant userMessageRepository save in ChatService ([bea8922](https://github.com/K444RU/match-me/commit/bea8922063001ecf70299ef5e935e855ec42018e))
* **UserCreationService:** fix bidirectional relationship when initing UserScore ([283a032](https://github.com/K444RU/match-me/commit/283a0322fcc41bd10ec53149a4d610295fa56f85))
* **UserCreationService:** fix bidirectional relationship when initing UserScore ([85bf226](https://github.com/K444RU/match-me/commit/85bf226215dca3ec98d2fae9be015a8c0dad6d63))
* **UserProfile, MatchingService:** fixed issues stemming from denormalizing DatingPool ID field ([3f13e3a](https://github.com/K444RU/match-me/commit/3f13e3a6b2507cddab7a61983ee16a44080a6d24))
* **UserProfile, MatchingService:** fixed issues stemming from denormalizing DatingPool ID field ([fb85bd2](https://github.com/K444RU/match-me/commit/fb85bd263bdb2b61a08f31b7836faeb0f1ac1bd6))
* **UserProfile:** revert hobbies field back to previous state ([812e3ad](https://github.com/K444RU/match-me/commit/812e3adf5ce08951fbc0e1f588fbd3aa52e68a0f))


### Documentation

* **DatingPool:** provide proper JavaDoc ([a33c13a](https://github.com/K444RU/match-me/commit/a33c13a32a7ee93d5d5696afc9ba56ae5fa6be31))
* **DatingPool:** provide proper JavaDoc ([bb0d9b5](https://github.com/K444RU/match-me/commit/bb0d9b54ba6eb60509d316523b15a18312eb98cb))
* **MatchingRepository:** add proper JavaDoc ([d2849df](https://github.com/K444RU/match-me/commit/d2849df545296ddb4fc3b6cf2861e2be1251f3d5))
* **MatchingRepository:** add proper JavaDoc ([22feedf](https://github.com/K444RU/match-me/commit/22feedfa1b9fa5e65de340f2b1e5b9ef64300f9f))
* **MatchingService:** add proper JavaDoc ([c3037e0](https://github.com/K444RU/match-me/commit/c3037e0b0425cd6e7797352a7e651a9391fb304e))
* **MatchingService:** add proper JavaDoc ([1ce0001](https://github.com/K444RU/match-me/commit/1ce000160264be5aaf53692ade325e388e295fb3))
* **UserAttributes:** add proper JavaDoc ([25db1e0](https://github.com/K444RU/match-me/commit/25db1e04a4a92df53a2a42e4bac5e803d49f4384))
* **UserAttributes:** add proper JavaDoc ([25d4812](https://github.com/K444RU/match-me/commit/25d4812dfce6a14ed89236086c81ee5b43a969fc))
* **UserPreferences:** add proper JavaDoc ([6ceaf2d](https://github.com/K444RU/match-me/commit/6ceaf2d21c32bbc865e4b82aa896349bff3d965f))
* **UserPreferences:** add proper JavaDoc ([16140f4](https://github.com/K444RU/match-me/commit/16140f4a8eba89dafa9c000a1e695c87a45f9d53))
* **UserProfile:** add proper JavaDoc ([110835f](https://github.com/K444RU/match-me/commit/110835f87c7c3b5574ab32262d959cb986b3db21))
* **UserProfile:** add proper JavaDoc ([6b803cc](https://github.com/K444RU/match-me/commit/6b803cc89d9abfaa7418df1c88f5c1ea83407a7e))
* **UserScore:** add proper JavaDoc ([afe33fc](https://github.com/K444RU/match-me/commit/afe33fce103dcff35acde7e363aab467eecec00a))
* **UserScore:** add proper JavaDoc ([4728c11](https://github.com/K444RU/match-me/commit/4728c11b942e532620190ab9896e4f7599df8a93))

## [0.1.0](https://github.com/karlromets/match-me/compare/back-end-0.0.9...back-end-0.1.0) (2025-02-06)


### Features

* **controller:** TERMINATE TESTCONTROLLER ([c5fe9a3](https://github.com/karlromets/match-me/commit/c5fe9a3831e515f8d166bcd9aff6490f4919006b))
* **dto:** add Builder annotation to ChatMessageResponseDTO ([8c76545](https://github.com/karlromets/match-me/commit/8c765450992fbe15ed5117ac6c7e9378eabf070c))
* **dto:** add Builder annotation to settings DTOs ([52d957b](https://github.com/karlromets/match-me/commit/52d957b2b14c34afebe538518ae1a31372dd92ef))


### Bug Fixes

* **service:** terminate user service helper ([dc71402](https://github.com/karlromets/match-me/commit/dc71402bf100a39ac3c6399f1a32a40be8a5d732))

## [0.0.9](https://github.com/karlromets/match-me/compare/back-end-0.0.8...back-end-0.0.9) (2025-01-31)


### Bug Fixes

* bemm09 CR resolved. ([5c6bb24](https://github.com/karlromets/match-me/commit/5c6bb24c89bbfd79e0c56da7c933498e2d5f5f9b))
* integrated Docker to to Spring Boot + React project to be able to build, test, and deploy quickly. ([4a88ddd](https://github.com/karlromets/match-me/commit/4a88dddcd06473366d42fa2a34b395546bc6aeef))
* updateAccount method missing existing number/email check ([007d745](https://github.com/karlromets/match-me/commit/007d745ce63eff0ff5efa6a35ea2217cedc6e641))

## [0.0.8](https://github.com/karlromets/match-me/compare/back-end-v0.0.8...back-end-0.0.8) (2025-01-25)


### Features

* back-end update ([3f1540c](https://github.com/karlromets/match-me/commit/3f1540ca59404165a8896471d29058ca20e3dd0b))
