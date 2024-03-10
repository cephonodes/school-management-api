#!/bin/bash

# テスト用のデータ投入スクリプト

facilitators_count=20
classrooms_count=10
students_count=400
min_facilitator_classes=1
max_facilitator_classes=3

# PostgreSQLの接続情報
pg_db="school"
pg_user="postgres"

# 先生のデータを生成
facilitators_values=""
for ((i=1; i<=facilitators_count; i++)); do
  facilitators_values+="(DEFAULT),"
done
facilitators_values="${facilitators_values%,}"
psql -U $pg_user -d $pg_db -c "INSERT INTO facilitators VALUES $facilitators_values;"

# クラスのデータを生成
classrooms_values=""
for ((i=1; i<=classrooms_count; i++)); do
  classrooms_values+="('クラス$i'),"
done
classrooms_values="${classrooms_values%,}"
psql -U $pg_user -d $pg_db -c "INSERT INTO classrooms (name) VALUES $classrooms_values;"

# 生徒のデータを生成
classroom_index=1
students_values=""
for ((i=1; i<=students_count; i++)); do
    students_values+="('生徒$i', 'student_$i', $classroom_index),"
    # 各生徒にクラスを順番に割り当てる
    ((classroom_index++))
    if [ "$classroom_index" -gt $classrooms_count ]; then
      classroom_index=1
    fi
done
students_values="${students_values%,}"
psql -U $pg_user -d $pg_db -c "INSERT INTO students (name, login_id, classroom_id) VALUES $students_values;"

# 先生とクラスの対応関係のデータを生成
relation_values=""
classroom_assignment_num=1
classroom_index=1
for ((i=1; i<=facilitators_count; i++)); do
  # 受け持つクラスの数だけクラスを順番に割り当てる
  for ((j=min_facilitator_classes; j<=classroom_assignment_num; j++)); do
    echo "classroom_assignment_num: ${classroom_assignment_num}"
    echo "classroom_index: ${classroom_index}"
    relation_values+="($i, $classroom_index),"
    ((classroom_index++))
    if [ "$classroom_index" -gt $classrooms_count ]; then
      classroom_index=1
    fi
  done
  # 受け持つクラス数を増やしていく
  ((classroom_assignment_num++))
  if [ "$classroom_assignment_num" -gt $max_facilitator_classes ]; then
    classroom_assignment_num=1
  fi
done
relation_values="${relation_values%,}"
psql -U $pg_user -d $pg_db -c "INSERT INTO facilitator_classroom_relation (facilitator_id, classroom_id) VALUES $relation_values;"

echo "テスト用のデータ投入が完了しました。"
