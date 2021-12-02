#  <img src="https://github.githubassets.com/images/icons/emoji/octocat.png" width="50"> **Team Tree** - Android Repository

![team_tree_intro_image](https://user-images.githubusercontent.com/80248474/136358672-d473dade-1483-4800-b663-c97e11aff541.png)

## Team Tree

+ Time line

>Since : 2020.10.11.
>
>21.08.31 - version : 'Stick Insect 1.0.1' [**released**](https://github.com/choco-tyranno/team-tree/blob/main/Release-note/Stick-Insect-1.0.1.md)
>
>
>
>21.11.26 - version : 'Stick Insect 1.1.1' [**released**](https://github.com/choco-tyranno/team-tree/blob/main/Release-note/Stick-Insect-1.1.1.md)
>
>~

- Contents:

  <img src = "https://user-images.githubusercontent.com/80248474/133612972-6ddd5b59-1319-4e0c-b0be-29a77becd90d.png" width = "100%">

  <h3> 이름과 별명, 연락처, 메모, 사진 등 인물 정보를 '카드'로 정리하고 수직형 포함 관계를 구성해 관리합니다. </h3>

  > 타이틀, 서브타이틀, 메모, 그리고 사용자 디바이스의 사진앱 또는 Team-tree의 external storage에 저장된 사진으로 카드를 구성하고, 해당 카드의 아래 공간(해당 카드에 종속되는 카드들의 공간)에 카드를 추가해 나가며 트리구조의 수직형 포함 관계를 만드는 앱입니다.
  >
  > 
  >
  > MVVM패턴의 프로젝트로 비동기처리된 이미지 로딩
  >
  > , Databinding과 Observable 객체들을 통한 Reactive한 UI update
  >
  > , ConstraintLayout으로 flat한 view hierarchy
  >
  > , DragDrop으로 카드 추가 및 카드 구조 이동
  >
  > , 카드 hierarchy 삭제
  >
  > , Search result 카드를 찾아가기
  >
  > , Nested RecyclerView 스크롤 상태 및 뷰 상태 관리
  >
  > , 사용자 Device별 반응형 View 사이즈 및 텍스트 사이즈 등을 구현했습니다.

- Demo

  [Team-tree app demo](https://github.com/choco-tyranno/team-tree/blob/main/Team-tree_app_demo.md)

- Keyword:
  + Android architecture Components[ViewModel, LiveData, DataBinding, Room]
  + Nested RecyclerView
  + Navigation
  + SearchView
  + Drag & Drop
  + Custom view
  + ConstraintLayout
  + Dynamic attribute setting



## Doc 

- ERD

  <img src="https://user-images.githubusercontent.com/80248474/136549484-06179bfd-c9b5-4041-bb7c-e2016146d1d4.png" width="100%">

  

- Rules

  - Commit convention

    | Type     | Content(Case)                                          |
    | -------- | ------------------------------------------------------ |
    | feat     | new feature has been added.                            |
    | fix      | bug has been fixed.                                    |
    | refactor | code style or structure has been refactored.           |
    | docs     | document or wiki has been modified.                    |
    | style    | style or component or page layout has been modified.   |
    | test     | test code has been modified or refactored.             |
    | chore    | buildscript or environment variable has been modified. |
    | resource | resource has been changed.                             |

    

  - Code convention

    - [Java code conventions](https://www.oracle.com/technetwork/java/codeconventions-150003.pdf)

    - [AOSP Java code style](https://source.android.com/setup/contribute/code-style)
    - [Android resources naming convention(by Jeroen Mols)](https://jeroenmols.com/blog/2016/03/07/resourcenaming/)


## Features

## Tech stack

## App architecture

 With 'Room' & Lifecycle aware 'ViewModel':

<img src="https://developer.android.com/codelabs/android-room-with-a-view/img/8e4b761713e3a76b.png">



## License

```
Copyright 2021 choco tyranno

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

