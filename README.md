# school-management-api
これは、学校の管理APIを題材に、KotlinでWeb APIを実装したものです。

## API仕様
実装されているAPIは1つのみです。

### GET /students
先生が担当するクラスに所属する生徒の情報を返します。

生徒の情報に対する部分一致検索、ページング、ソートができます。

#### Parameter
- facilitator_id (required): 先生のID
- page (optional): 表示するページ番号（デフォルト値は1）
- limit (optional): ページに表示するデータ数（デフォルト値は1）
- sort (optional): ソートキー（生徒の名前：name, 生徒のログインID：loginId）
- order (optional): ソートの順番（昇順：asc, 降順：desc デフォルト値はasc）
- name_like (optional): 生徒の名前に対する検索文字列（部分一致）
- loginId_like (optional): 生徒のログインIDに対する検索文字列（部分一致）

注意点：

- orderを指定せずにsortを指定することはできますが、sortを指定せずにorderのみを指定された場合は、ステータスコード400を返します。
- name_likeとloginId_likeはどちらかのみ指定できます。両方指定された場合は、ステータスコード400を返します。
- pageが範囲外だった場合は、ステータスコード400を返します。
- ソートは文字列に対するソートです。数字の部分は昇順の場合「"1", "101", "11", "2", ...」のような順番で並ぶことになります。

#### Response
##### 成功時
ステータスコード：200

レスポンスボディ：
```json
{
  "students":[
    {
      "id":1,
      "name":"生徒1",
      "loginId":"student_1",
      "classroom":{
        "id":1,
        "name":"特進クラス"
      }
    }
  ],
  "totalCount": 1
}
```

##### パラメーターが不正な場合
ステータスコード：400

## 動作条件
- Dockerとdocker-composeがインストールされていること
  （Docker：25.0.3、docker-compose：v2.24.5-desktop.1で動作確認済み）

## 実行方法
本リポジトリをローカルにcloneしてから、以下のコマンドを実行してください。
アプリケーションのビルド、DBの起動、DBへのサンプルデータの挿入が行われ、Webサーバーが起動します。

```bash
cd school-management-api
docker compose up
```

## 動作確認方法
以下に、動作確認のためのサンプルデータ（コマンド）とレスポンスの組を記載します。

**コマンドには、結果を見やすくするためにjqを使っています。**

DB内のサンプルデータの内訳は、CSV形式で./doc/sample_data内に置いています。


先生1のIDのみでデータを取得
```bash
curl "http://127.0.0.1:8080/students?facilitator_id=1" | jq
```

```json
{
  "students": [
    {
      "id": 1,
      "name": "生徒1",
      "loginId": "student_400",
      "classroom": {
        "id": 1,
        "name": "クラス1"
      }
    }
  ],
  "totalCount": 1
}
```

1ページあたり4件として、1ページ目のデータを取得
```bash
curl "http://127.0.0.1:8080/students?facilitator_id=1&page=1&limit=4" | jq
```

```json
{
  "students": [
    {
      "id": 1,
      "name": "生徒1",
      "loginId": "student_400",
      "classroom": {
        "id": 1,
        "name": "クラス1"
      }
    },
    {
      "id": 101,
      "name": "生徒101",
      "loginId": "student_300",
      "classroom": {
        "id": 1,
        "name": "クラス1"
      }
    },
    {
      "id": 11,
      "name": "生徒11",
      "loginId": "student_390",
      "classroom": {
        "id": 1,
        "name": "クラス1"
      }
    },
    {
      "id": 111,
      "name": "生徒111",
      "loginId": "student_290",
      "classroom": {
        "id": 1,
        "name": "クラス1"
      }
    }
  ],
  "totalCount": 4
}
```

1ページあたり2件として、2ページ目のデータを取得
```bash
curl "http://127.0.0.1:8080/students?facilitator_id=1&page=2&limit=2" | jq
```

```json
{
  "students": [
    {
      "id": 11,
      "name": "生徒11",
      "loginId": "student_390",
      "classroom": {
        "id": 1,
        "name": "クラス1"
      }
    },
    {
      "id": 111,
      "name": "生徒111",
      "loginId": "student_290",
      "classroom": {
        "id": 1,
        "name": "クラス1"
      }
    }
  ],
  "totalCount": 2
}
```

先生2について、生徒の名前で昇順にソートしたデータを取得する
```bash
curl "http://127.0.0.1:8080/students?facilitator_id=2&page=1&limit=3&sort=name&order=asc" | jq
```

```json
{
  "students": [
    {
      "id": 102,
      "name": "生徒102",
      "loginId": "student_299",
      "classroom": {
        "id": 2,
        "name": "クラス2"
      }
    },
    {
      "id": 103,
      "name": "生徒103",
      "loginId": "student_298",
      "classroom": {
        "id": 3,
        "name": "クラス3"
      }
    },
    {
      "id": 112,
      "name": "生徒112",
      "loginId": "student_289",
      "classroom": {
        "id": 2,
        "name": "クラス2"
      }
    }
  ],
  "totalCount": 3
}
```

先生2について、生徒の名前で降順にソートしたデータを取得する
```bash
curl "http://127.0.0.1:8080/students?facilitator_id=2&page=1&limit=3&sort=name&order=desc" | jq
```

```json
{
  "students": [
    {
      "id": 93,
      "name": "生徒93",
      "loginId": "student_308",
      "classroom": {
        "id": 3,
        "name": "クラス3"
      }
    },
    {
      "id": 92,
      "name": "生徒92",
      "loginId": "student_309",
      "classroom": {
        "id": 2,
        "name": "クラス2"
      }
    },
    {
      "id": 83,
      "name": "生徒83",
      "loginId": "student_318",
      "classroom": {
        "id": 3,
        "name": "クラス3"
      }
    }
  ],
  "totalCount": 3
}
```

先生2について、生徒のログインIDで昇順にソートしたデータを取得する
```bash
curl "http://127.0.0.1:8080/students?facilitator_id=2&page=1&limit=3&sort=loginId&order=asc" | jq
````

```json
{
  "students": [
    {
      "id": 293,
      "name": "生徒293",
      "loginId": "student_108",
      "classroom": {
        "id": 3,
        "name": "クラス3"
      }
    },
    {
      "id": 292,
      "name": "生徒292",
      "loginId": "student_109",
      "classroom": {
        "id": 2,
        "name": "クラス2"
      }
    },
    {
      "id": 283,
      "name": "生徒283",
      "loginId": "student_118",
      "classroom": {
        "id": 3,
        "name": "クラス3"
      }
    }
  ],
  "totalCount": 3
}
```

先生2について、生徒のログインIDで降順にソートしたデータを取得する
```bash
curl "http://127.0.0.1:8080/students?facilitator_id=2&page=1&limit=3&sort=loginId&order=desc" | jq
````

```json
{
  "students": [
    {
      "id": 302,
      "name": "生徒302",
      "loginId": "student_99",
      "classroom": {
        "id": 2,
        "name": "クラス2"
      }
    },
    {
      "id": 303,
      "name": "生徒303",
      "loginId": "student_98",
      "classroom": {
        "id": 3,
        "name": "クラス3"
      }
    },
    {
      "id": 392,
      "name": "生徒392",
      "loginId": "student_9",
      "classroom": {
        "id": 2,
        "name": "クラス2"
      }
    }
  ],
  "totalCount": 3
}
```

先生2について、生徒の名前で部分一致検索し、生徒の名前で昇順にソートしたデータを取得する
```bash
curl "http://127.0.0.1:8080/students?facilitator_id=2&page=1&limit=5&sort=name&order=asc&name_like=10" | jq
```

```json
{
  "students": [
    {
      "id": 102,
      "name": "生徒102",
      "loginId": "student_299",
      "classroom": {
        "id": 2,
        "name": "クラス2"
      }
    },
    {
      "id": 103,
      "name": "生徒103",
      "loginId": "student_298",
      "classroom": {
        "id": 3,
        "name": "クラス3"
      }
    }
  ],
  "totalCount": 2
}
```

先生2について、生徒のログインIDで部分一致検索し、生徒のログインIDで昇順にソートしたデータを取得する
```bash
curl "http://127.0.0.1:8080/students?facilitator_id=2&page=1&limit=5&sort=loginId&order=asc&loginId_like=10" | jq
```

```json
{
  "students": [
    {
      "id": 293,
      "name": "生徒293",
      "loginId": "student_108",
      "classroom": {
        "id": 3,
        "name": "クラス3"
      }
    },
    {
      "id": 292,
      "name": "生徒292",
      "loginId": "student_109",
      "classroom": {
        "id": 2,
        "name": "クラス2"
      }
    }
  ],
  "totalCount": 2
}
```

条件に合致するデータが1件も無いパターン
```bash
curl "http://127.0.0.1:8080/students?facilitator_id=21" | jq
```

```json
{
  "students": [],
  "totalCount": 0
}
```

パラメーターが不正なパターン（ステータスコード400）
```bash
curl -i "http://127.0.0.1:8080/students"
```

```
HTTP/1.1 400 Bad Request
Content-Length: 0
```

## アーキテクチャの簡単な説明
### インフラ

### アプリケーション
オニオンアーキテクチャを採用しました。
