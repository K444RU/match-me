# Changelog

All notable changes to this project will be documented in this file. See [commit-and-tag-version](https://github.com/absolute-version/commit-and-tag-version) for commit guidelines.

## [0.0.1-alpha.7](https://gitea.kood.tech/karlrometsomelar/match-me/compare/v0.0.1-alpha.6...v0.0.1-alpha.7) (2024-12-12)


### Features

* add .env, citysuggestions, types, geocoding util ([0958a9f](https://gitea.kood.tech/karlrometsomelar/match-me/commit/0958a9fd1766ba7b13e8a76254c72c00846261a3))
* add custom exceptions ([7583b0d](https://gitea.kood.tech/karlrometsomelar/match-me/commit/7583b0d4b1d88be72a19fc244d8d5a1cdc85065c))
* add custom validation logic + filestructure ([84d38bc](https://gitea.kood.tech/karlrometsomelar/match-me/commit/84d38bc6da5ccd3d75b1baf161f63b8e1e90eab8))
* add duplicate email exception ([3500bfd](https://gitea.kood.tech/karlrometsomelar/match-me/commit/3500bfde45a477ab5ee8fa8106feb409c5df623d))
* add duplicate number validation ([665214b](https://gitea.kood.tech/karlrometsomelar/match-me/commit/665214b1a0a61f2c9114656b59705d3852a2586a))
* add endpoints to retrieve entities related to user ([b464d32](https://gitea.kood.tech/karlrometsomelar/match-me/commit/b464d325915dea424f73d76c616fbd3f7467a82d))
* add firstName, lastName, and alias fields ([be54cce](https://gitea.kood.tech/karlrometsomelar/match-me/commit/be54cce972e6420f2511def48ca0cc9d78985ee9))
* add insertion for all types ([3b6c93f](https://gitea.kood.tech/karlrometsomelar/match-me/commit/3b6c93f3288f54fc202402726fd90843a7914cd9))
* add json parse exception ([10374f9](https://gitea.kood.tech/karlrometsomelar/match-me/commit/10374f954d1a5d181330da9cf098109ef3b0d5fd))
* add onFocus and onBlur props to InputField component ([c244be9](https://gitea.kood.tech/karlrometsomelar/match-me/commit/c244be957a90bdf866354739e7ec434185d9a58a))
* add types unit tests ([cd86120](https://gitea.kood.tech/karlrometsomelar/match-me/commit/cd86120f4169191f9418b2c7b10735d60eb41239))
* add useDebounce hook ([a232b52](https://gitea.kood.tech/karlrometsomelar/match-me/commit/a232b527d2fac79744fcf75501f788056b0f9c59))
* ask firstName,lastName and alias ([5d19ad5](https://gitea.kood.tech/karlrometsomelar/match-me/commit/5d19ad52a5303423dffe2a1ae226408cbe076c5a))
* integrate liquibase for data insertion ([8b76235](https://gitea.kood.tech/karlrometsomelar/match-me/commit/8b7623596f5c6b806a338949de308137d739bc4c))


### Bug Fixes

* account create returns 201 ([cb362fb](https://gitea.kood.tech/karlrometsomelar/match-me/commit/cb362fbae5aab4439457b1ae1815868409f34b85))
* add missing user_attributes_id to profilechange ([77dca34](https://gitea.kood.tech/karlrometsomelar/match-me/commit/77dca34aa4a8e3ac5259938bcebaeb7d6a16477c))
* authToken was stored in double quotes ([7382fb3](https://gitea.kood.tech/karlrometsomelar/match-me/commit/7382fb3bb07d501c518a75cf17e144aa255c74ef))
* datepicker utc conversion ([dd665d0](https://gitea.kood.tech/karlrometsomelar/match-me/commit/dd665d04d8619034d4a79f1dc76b939bb6e226fc))
* fix broken UserServiceTest ([5887027](https://gitea.kood.tech/karlrometsomelar/match-me/commit/58870279a83e1ce0f631647f87f5d97336ba60bb))
* got auth working with array ([a2593d5](https://gitea.kood.tech/karlrometsomelar/match-me/commit/a2593d54394e4efca51f3c86a5154c7170de1385))
* motion() is deprecated ([d1a91fd](https://gitea.kood.tech/karlrometsomelar/match-me/commit/d1a91fd4f6277eab3a348ba2825cd1655d146d78))
* mvn crash on startup ([a2705e7](https://gitea.kood.tech/karlrometsomelar/match-me/commit/a2705e765a0dde4c1745a939ea3ae6cacb791869))
* navigate from preferences reg. -> chatPage ([9cf6ab2](https://gitea.kood.tech/karlrometsomelar/match-me/commit/9cf6ab29cbadbe221849a89acfd1c77d75b5c195))
* remove circular dependencies ([f7f00e3](https://gitea.kood.tech/karlrometsomelar/match-me/commit/f7f00e3dde156939c72ea7b41ab799f203dacbd7))
* remove profileData on reg. completion ([9ec1c50](https://gitea.kood.tech/karlrometsomelar/match-me/commit/9ec1c500be9be3588e95aa897ecf311639773286))
* remove unneeded postgresql dialect ([db51a96](https://gitea.kood.tech/karlrometsomelar/match-me/commit/db51a962ceed43478624fa4fc524dcfa7f2065df))
* setUserParameters w/ initial account ([124ae1b](https://gitea.kood.tech/karlrometsomelar/match-me/commit/124ae1b6c9a32538e146cd82589afda713723cc7))
* user role -> [@manytomany](https://gitea.kood.tech/manytomany) ([c42fa11](https://gitea.kood.tech/karlrometsomelar/match-me/commit/c42fa11f1afa73db42043365d8bab89b81d7c399))
* UserServiceTest ([d9ccf9d](https://gitea.kood.tech/karlrometsomelar/match-me/commit/d9ccf9db6595453270828226a84b44311c663d39))


### Housekeeping

* add multi-range-slider-react ([e3519da](https://gitea.kood.tech/karlrometsomelar/match-me/commit/e3519dadc84f91213b1b6d035f8064b8fb9114d6))
* add todo ([bf1c918](https://gitea.kood.tech/karlrometsomelar/match-me/commit/bf1c918c2826af14ee1568a181ccda45d381dacc))
* clean up back-end import statements. ([374ff7e](https://gitea.kood.tech/karlrometsomelar/match-me/commit/374ff7e628e77422d22db84b617f8995b90719cc))
* remove table rename fix ([31747a4](https://gitea.kood.tech/karlrometsomelar/match-me/commit/31747a4d955efc43003ec8de2e04d83abc161648))
* remove unused import ([cf1bc29](https://gitea.kood.tech/karlrometsomelar/match-me/commit/cf1bc29f9bed0d92441aac9399457f8c89cb4cc4))


### Styling

* add spinner ([eb49c91](https://gitea.kood.tech/karlrometsomelar/match-me/commit/eb49c91ec3e9f39f82c38eef848e921ec4d4ef00))
* remove disgusting background color ([9f92cfb](https://gitea.kood.tech/karlrometsomelar/match-me/commit/9f92cfbfcf3b996bfdc491d9743774171f59cb4b))
* run prettier ([47ae560](https://gitea.kood.tech/karlrometsomelar/match-me/commit/47ae560641d63eea50a9c8adcd4e7597eae5a1ac))
* tabWidth to 4 ([b030069](https://gitea.kood.tech/karlrometsomelar/match-me/commit/b030069aa222befe5b8a3a9f51afc6253cdc8b00))


### Refactoring

* camelcase to snakecase ([d76af7a](https://gitea.kood.tech/karlrometsomelar/match-me/commit/d76af7a7fff206794ae38fdbafb724a65a8cdf70))
* change gender to record ([5fe4ae5](https://gitea.kood.tech/karlrometsomelar/match-me/commit/5fe4ae506533d1c6729822b90aa0e16eb4cc5ef9))
* move chatspage ([84c0cca](https://gitea.kood.tech/karlrometsomelar/match-me/commit/84c0ccaac4ed89060fd0e5142a3ab57615444c75))
* profile-completion ([640722a](https://gitea.kood.tech/karlrometsomelar/match-me/commit/640722acc442ad29beb4fba488c936a78ea6e103))
* records back to classes ([fd863a6](https://gitea.kood.tech/karlrometsomelar/match-me/commit/fd863a65e6aad74b1e89e9f239af81f4d816968a))
* refactor back-end ([efb6ab7](https://gitea.kood.tech/karlrometsomelar/match-me/commit/efb6ab7e5f362864e8828b753022f5598f714cad))
* refactor back-stage entities and basic R operations ([f5750a7](https://gitea.kood.tech/karlrometsomelar/match-me/commit/f5750a7f48a14b47d0898e2926106cb603d7ecaf))
* Refactor some elements ([5aa1c4b](https://gitea.kood.tech/karlrometsomelar/match-me/commit/5aa1c4b242048917372f9e30a3b2577ebc184068))
* rename dup email to dup field ([c6a9a3d](https://gitea.kood.tech/karlrometsomelar/match-me/commit/c6a9a3db6ef51c79511b0efa67ab1cd0a969bc93))
* too much to describe ([4a517fd](https://gitea.kood.tech/karlrometsomelar/match-me/commit/4a517fd0d00359a4a523ca18f6b5f9f80752a010))
* use jwt instead of userid ([1e7ff04](https://gitea.kood.tech/karlrometsomelar/match-me/commit/1e7ff049dc7290e9cf3d4f1ef719263fc4223bbf))

## [0.0.1-alpha.6](https://gitea.kood.tech/karlrometsomelar/match-me/compare/v0.0.1-alpha.5...v0.0.1-alpha.6) (2024-11-24)


### Features

* add button component ([f767b93](https://gitea.kood.tech/karlrometsomelar/match-me/commit/f767b93feac0b54e6228d63559b6b6e29976dac5))
* add favicon ([2b94c1f](https://gitea.kood.tech/karlrometsomelar/match-me/commit/2b94c1f5a3b8cdac1f951767d420d7f01ac2fd1b))
* add genders & sexuality list ([f6b9a23](https://gitea.kood.tech/karlrometsomelar/match-me/commit/f6b9a232bf791ba15f63f329dbe4412f826096f2))
* add option to disabled select component ([d65361c](https://gitea.kood.tech/karlrometsomelar/match-me/commit/d65361c822b8e44aa6f1f4b46253ae49b643286d))
* add profile-completion ([cac4ef6](https://gitea.kood.tech/karlrometsomelar/match-me/commit/cac4ef640901626b8f1adeacb8a84ebaf2fc85bd))
* add select component ([c43d6ea](https://gitea.kood.tech/karlrometsomelar/match-me/commit/c43d6ead13c459bf90eae355ce11a31ad5faf8b8))
* add Sign Up to nav ([8d10666](https://gitea.kood.tech/karlrometsomelar/match-me/commit/8d106665b88378ad6dce30b6b9df2979a2f51f45))


### Bug Fixes

* add countryCode to RegForm ([c8f2e07](https://gitea.kood.tech/karlrometsomelar/match-me/commit/c8f2e0759e6f8a2ee529448b7a94a1293e69f332))


### Housekeeping

* add [@assets](https://gitea.kood.tech/assets) ([626f366](https://gitea.kood.tech/karlrometsomelar/match-me/commit/626f3664de2761ccada98223e250aeed2fe35ae8))
* add [@ui](https://gitea.kood.tech/ui) ([dbafaf8](https://gitea.kood.tech/karlrometsomelar/match-me/commit/dbafaf876c0701c399e2d050738a50fc0783ab60))
* add date-fns ([064d979](https://gitea.kood.tech/karlrometsomelar/match-me/commit/064d9794b3ae07c0369ce058f3e38333df3387c7))
* add react-daypicker ([0ad0955](https://gitea.kood.tech/karlrometsomelar/match-me/commit/0ad09554a82827d4fc1750e287922e1ca5f7c2cb))
* remove weird hover effect ([a640c71](https://gitea.kood.tech/karlrometsomelar/match-me/commit/a640c717504226a33f1c42d1cb6136bd695c8876))
* update import ([3307530](https://gitea.kood.tech/karlrometsomelar/match-me/commit/3307530170400c56770848294a51d7db0d7a68e4))


### Styling

* add emphasis on hero ([138672b](https://gitea.kood.tech/karlrometsomelar/match-me/commit/138672b3e797b318e79b277cae2fb1283bd26a67))
* hero rework again ([ce23bca](https://gitea.kood.tech/karlrometsomelar/match-me/commit/ce23bca7aabad1660614f61b0980abc084be8529))
* linkbutton gesture effex ([d0c4eb5](https://gitea.kood.tech/karlrometsomelar/match-me/commit/d0c4eb50c8fb8bbfe0179fc39fdc53fa5a808923))
* modify /register & /login ([4521864](https://gitea.kood.tech/karlrometsomelar/match-me/commit/4521864c6cf78fbaeebd9e6b6d1576977492fa36))
* modify Hero ([a1a9847](https://gitea.kood.tech/karlrometsomelar/match-me/commit/a1a98471fbe695ca044c3396741b1ecc8ad2fecb))
* remove mb-3 ([989cf9d](https://gitea.kood.tech/karlrometsomelar/match-me/commit/989cf9d6f1aeacfb0a8f94a432f6314c8b29d28e))


### Refactoring

* age -> localdate ([d8a8938](https://gitea.kood.tech/karlrometsomelar/match-me/commit/d8a893858d5a5b6e42e953d53f123b76f9c1a17d))
* move UserAttr. & Pref. ([b796659](https://gitea.kood.tech/karlrometsomelar/match-me/commit/b796659a777053acd0f66663b6cbb8fd8fac669f))

## [0.0.1-alpha.5](https://gitea.kood.tech/karlrometsomelar/match-me/compare/v0.0.1-alpha.4...v0.0.1-alpha.5) (2024-11-24)


### Features

* add auth state ([b066027](https://gitea.kood.tech/karlrometsomelar/match-me/commit/b0660273b31bd1883e02b4e789d0f0406fa01d8e))
* add OneHandleSlider ([6b5ea6d](https://gitea.kood.tech/karlrometsomelar/match-me/commit/6b5ea6d248eab226cfb25094bebac2148aee3c87))
* add UserAttributes as a settings component. ([13fcfd7](https://gitea.kood.tech/karlrometsomelar/match-me/commit/13fcfd7274958b9a771493ed06d5c081a8fecc24))
* Link component using button styling ([493cbf7](https://gitea.kood.tech/karlrometsomelar/match-me/commit/493cbf70df0a332e2ed147152c771973e0f2cd2f))
* navbar use auth state ([2aa7736](https://gitea.kood.tech/karlrometsomelar/match-me/commit/2aa7736f60eaa394f6258cc000716c798ad00f65))


### Bug Fixes

* import typos causing mvn run fail... ([5fd9ddb](https://gitea.kood.tech/karlrometsomelar/match-me/commit/5fd9ddb12135644ca04e16a7c860c2847aaaf3b8))
* signup/new user creation ([8f840ed](https://gitea.kood.tech/karlrometsomelar/match-me/commit/8f840eda444ef5e8540664a52172726fd3c26f20))


### Refactoring

* modify user array roles -> string role ([b903f45](https://gitea.kood.tech/karlrometsomelar/match-me/commit/b903f454d039f0775b5313b62e33c30789205597))
* restructure back-end ([76ddcfc](https://gitea.kood.tech/karlrometsomelar/match-me/commit/76ddcfcc73af7528d18f1361b675347c7f6ebf9b))

## [0.0.1-alpha.4](https://gitea.kood.tech/karlrometsomelar/match-me/compare/v0.0.1-alpha.3...v0.0.1-alpha.4) (2024-11-22)


### Features

* add database entities ([b29395b](https://gitea.kood.tech/karlrometsomelar/match-me/commit/b29395b0a4b685159a6c39fb7bc4e0e6264f49c9))
* add UserMessages, MessageEvents, MessageEventTypes ([2022775](https://gitea.kood.tech/karlrometsomelar/match-me/commit/202277545bb1c3fd5a267270014589918373293b))
* add UserProfile and related entities. ([8f5616f](https://gitea.kood.tech/karlrometsomelar/match-me/commit/8f5616fbc933c84f92871c5ed6d862341750a421))

## [0.0.1-alpha.3](https://gitea.kood.tech/karlrometsomelar/match-me/compare/v0.0.1-alpha.2...v0.0.1-alpha.3) (2024-11-22)


### Features

* add dynamic FormResponse state ([e303c01](https://gitea.kood.tech/karlrometsomelar/match-me/commit/e303c01902f1e39b798c6edde223a503db59e5a6))
* add FormResponse component for authentication ([0445584](https://gitea.kood.tech/karlrometsomelar/match-me/commit/04455849f8877ad5494587c5d811f3e1e73c68ea))
* add InputScroll.tsx ([0f72305](https://gitea.kood.tech/karlrometsomelar/match-me/commit/0f72305eaf4d7b760a9e939de92a2f01223b78d1))
* add proper auth w/ java backend w/ axios ([4b94480](https://gitea.kood.tech/karlrometsomelar/match-me/commit/4b94480f661d106f22ad622f8e1e879317809152))
* add user property activity ([c473df1](https://gitea.kood.tech/karlrometsomelar/match-me/commit/c473df1dfc0f4545fc2c546df9151bb417602444))


### Styling

* add jumptotop animation & state ([b2ff085](https://gitea.kood.tech/karlrometsomelar/match-me/commit/b2ff08588942d25c910c532135588e3f30800587))
* run prettier ([e960b9f](https://gitea.kood.tech/karlrometsomelar/match-me/commit/e960b9f46ac7775cf18ede4635bd3ba346bdada7))


### Refactoring

* add AuthService & [@service](https://gitea.kood.tech/service) ([7694480](https://gitea.kood.tech/karlrometsomelar/match-me/commit/7694480e57f0e35f11ee1eb331ac93e3d67d22cf))
* big restructure of project ([0258c00](https://gitea.kood.tech/karlrometsomelar/match-me/commit/0258c0076d22fbc782cc142f4e5348f4b2cd9310))
* connect front-end registration w backend ([5cf12f0](https://gitea.kood.tech/karlrometsomelar/match-me/commit/5cf12f0b26deb46279d88dee0f5cfb3fdb50ada4))
* modify InputField ([a33a1a3](https://gitea.kood.tech/karlrometsomelar/match-me/commit/a33a1a304ce25e27d5d49f7ef77127ed1520e799))
* Rename Login.tsx to PopUpForm.tsx ([ce30a0f](https://gitea.kood.tech/karlrometsomelar/match-me/commit/ce30a0f3576d5e4961d172f975c8326e2b71cb3a))

## [0.0.1-alpha.2](https://gitea.kood.tech/karlrometsomelar/match-me/compare/v0.0.1-alpha.1...v0.0.1-alpha.2) (2024-11-21)


### Features

* add exception handler ([63aca75](https://gitea.kood.tech/karlrometsomelar/match-me/commit/63aca7596de0f9a4f8357b32ba48fb170398a013))
* add unit testing ([8d9c78f](https://gitea.kood.tech/karlrometsomelar/match-me/commit/8d9c78f32dcc96c3de549b382599e5d240d7619b))


### Bug Fixes

* back-end now throws 401 or 403 for prot. routes ([1787873](https://gitea.kood.tech/karlrometsomelar/match-me/commit/1787873c54c7a7f49de72eee1f0d8f817b19cf22))


### Housekeeping

* add devtools dependency for live reload ([1394dc3](https://gitea.kood.tech/karlrometsomelar/match-me/commit/1394dc329bb6d330359e60362654ee30442e5d98))
* add unit testing ([9b30e49](https://gitea.kood.tech/karlrometsomelar/match-me/commit/9b30e49009e5442bd28a7e59e144eb856c61147f))


### Documentation

* add explaining comments, TODOs ([6a68d35](https://gitea.kood.tech/karlrometsomelar/match-me/commit/6a68d3549c449bad07489c1520c853c3abbe7e78))


### Refactoring

* add lombok ([3d0ce46](https://gitea.kood.tech/karlrometsomelar/match-me/commit/3d0ce46dfbd3269772d23515b4a39c442bdb7ed2))
* Separated Inputfield component from forms ([898d8a5](https://gitea.kood.tech/karlrometsomelar/match-me/commit/898d8a56a372874e8bb7c4e56eb375d711f602e4))

## [0.0.1-alpha.1](https://gitea.kood.tech/karlrometsomelar/match-me/compare/v0.0.1-alpha.0...v0.0.1-alpha.1) (2024-11-19)


### Features

* add backend jwt generation and password hashing ([cb3f50b](https://gitea.kood.tech/karlrometsomelar/match-me/commit/cb3f50b8e48da486b386d4e6a707d78fbea2427d))


### Bug Fixes

* added postgres and fixed back-end build ([f1bbe8a](https://gitea.kood.tech/karlrometsomelar/match-me/commit/f1bbe8ae6486a086971e551515ddbaf25e3faf63))
* close overlay after regiter/login ([14f1590](https://gitea.kood.tech/karlrometsomelar/match-me/commit/14f1590b42ce1ef12278783a8cfd345ffd27ecd0))


### Housekeeping

* add form_utilities dir for clear structure ([299cfba](https://gitea.kood.tech/karlrometsomelar/match-me/commit/299cfba4eabcfab032cb0a664e0eca6630f98e0a))
* added inputfield backbone and moving to shared workingspace ([e08c139](https://gitea.kood.tech/karlrometsomelar/match-me/commit/e08c139c69c117aa12ed59067686ce5bcbd1c12f))
* setup java ([b64678c](https://gitea.kood.tech/karlrometsomelar/match-me/commit/b64678cb618fadf6ba40b88aa67e710b95d073df))


### Styling

* run prettier ([b473320](https://gitea.kood.tech/karlrometsomelar/match-me/commit/b473320be67bfcac3571777c52186afbe4b06607))


### Refactoring

* setting up chatsPage to get incoming data ([de8ab61](https://gitea.kood.tech/karlrometsomelar/match-me/commit/de8ab61553ac354d8befd2d046df27f29930cb7a))

## [0.0.1-alpha.0](https://gitea.kood.tech/karlrometsomelar/match-me/compare/v0.0.0...v0.0.1-alpha.0) (2024-11-18)


### Features

* add "Learn More" button functionality ([9447186](https://gitea.kood.tech/karlrometsomelar/match-me/commit/94471865a922d90c7693d6348fbb5defcc10d79a))
* add authentication ([5824fcb](https://gitea.kood.tech/karlrometsomelar/match-me/commit/5824fcb4c86be5b14b038a5a0521cf24c9dd5493))
* add homepage jumptotopbutton ([7104e81](https://gitea.kood.tech/karlrometsomelar/match-me/commit/7104e8102c26536d101ba0a5d0730912003184a9))
* login & authentication, animation ([5e1304e](https://gitea.kood.tech/karlrometsomelar/match-me/commit/5e1304e7e0cb2ffa439ef9a59ced6803eec43bfb))


### Bug Fixes

* add missing google fonts link ([a5b5dd7](https://gitea.kood.tech/karlrometsomelar/match-me/commit/a5b5dd7b8f9431bdeedb249d259203fe1e36d12a))
* multiple ui issues ([5a341ff](https://gitea.kood.tech/karlrometsomelar/match-me/commit/5a341ff30da5bd078af8347d580425f99dd9f296))


### Housekeeping

* add db.json objects ([15cb6d5](https://gitea.kood.tech/karlrometsomelar/match-me/commit/15cb6d545da6fa74d6ce2c76ed889525cfce7135))
* configure tailwind theme colors ([273e6dd](https://gitea.kood.tech/karlrometsomelar/match-me/commit/273e6dda21d995163150674e67c68aa14dbd1513))
* create necessary backbone for login functionality ([bee05e4](https://gitea.kood.tech/karlrometsomelar/match-me/commit/bee05e438fb8d521aaf4e5024ac86bea91ae75ef))
* implement react-router-dom ([8b429f0](https://gitea.kood.tech/karlrometsomelar/match-me/commit/8b429f098cc307884641bf1e4d78cf1aea5f730c))
* remove unused imports ([c9e8d80](https://gitea.kood.tech/karlrometsomelar/match-me/commit/c9e8d80328c8f470335e334396791c06e57e5717))
* removing unused code & values ([900c4b3](https://gitea.kood.tech/karlrometsomelar/match-me/commit/900c4b368926493e8b301009c1f68973fd8b7454))


### Styling

* run prettier ([e821a80](https://gitea.kood.tech/karlrometsomelar/match-me/commit/e821a8054bbc1c0f26a7f923e87c253b49eae2d6))


### Refactoring

* proper defaults for hero component ([600e547](https://gitea.kood.tech/karlrometsomelar/match-me/commit/600e547bff2b96d35e98b810ed118b95caab299b))


### Testing

* add json-server ([99eea1a](https://gitea.kood.tech/karlrometsomelar/match-me/commit/99eea1ab26b2aa43f7b08f099c5a5263c089d877))

## 0.0.0 (2024-11-17)


### Housekeeping

* add .versionrc ([bf0fd0f](https://gitea.kood.tech/karlrometsomelar/match-me/commit/bf0fd0fe9f1069ac2b79064a8416ad171ffb795d))
* configuring build tools ([7bb595f](https://gitea.kood.tech/karlrometsomelar/match-me/commit/7bb595fb7200f0e0adf0ea4ccde35b73760f104e))
