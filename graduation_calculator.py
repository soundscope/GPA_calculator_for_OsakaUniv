from tkinter import filedialog
typ = [('open SIK***.csv','*.csv')]
path = filedialog.askopenfilename(filetypes = typ, initialdir = dir) 

import csv
#division -> your earned credits
earned_division_credit = {}

# One line of SIK***.csv is formed like following 3 sentences
#"学生所属コード","学籍番号","画面指定年度","画面指定学期","No.",
#"科目詳細区分","科目小区分","開講科目名 ","リーディングプログラム科目",
#"知のジムナスティックス科目","単位数","修得年度","修得学期","評語","合否"

with open(path, encoding='shift_jis') as csvfile:
    csv_matrix = list(csv.reader(csvfile))

    #strip unnecessary top lines
    csv_matrix = csv_matrix[csv_matrix.index(
        ['学生所属コード', '学籍番号', '画面指定年度', '画面指定学期', 'No.', 
        '科目詳細区分', '科目小区分', '開講科目名 ', 'リーディングプログラム科目', 
        '知のジムナスティックス科目', '単位数', '修得年度', '修得学期', '評語', '合否']) + 1:]

    #strip unnecessary falled classes
    csv_matrix = [x for x in csv_matrix if x[14] == "合"]

    for row in csv_matrix:
        division = row[5]+row[6]

        #exception
        if  "国際性涵養教育系科目第２外国語" in division :
            division = "国際性涵養教育系科目第２外国語" 

        credit = int(row[10])
        if division in earned_division_credit: earned_division_credit[division] += credit
        else: earned_division_credit[division] = credit

#for Information Science Students  (entered in 2019)
#division -> requirements of credits
graduation_requirements = {
    "専門教育系科目（専門教育科目）必修": 62 ,
    "専門教育系科目（専門教育科目）選択Ａ群": 8 ,
    "専門教育系科目（専門教育科目）選択Ｂ群": 8 ,
    "専門教育系科目（専門教育科目）選択Ｃ群": 6 ,
    "教養教育系科目学問への扉": 2 ,
    "教養教育系科目基盤教養教育科目": 6 ,
    "教養教育系科目（高度教養教育科目）選択必修": 2 ,
    "教養教育系科目情報教育科目": 2 ,
    "教養教育系科目健康・スポーツ教育科目（必修）": 1 ,
    "教養教育系科目健康・スポーツ教育科目（選択）": 1 ,
    "専門教育系科目（専門基礎教育科目）必修":  12 ,
    "専門教育系科目（専門基礎教育科目）選択": 6 ,
    "国際性涵養教育系科目第１外国語（総合英語）": 6 ,
    "国際性涵養教育系科目第１外国語（実践英語）": 2 ,
    "国際性涵養教育系科目第２外国語": 3 ,
    "国際性涵養教育系科目グローバル理解": 2 ,
    "国際性涵養教育系科目（高度国際性涵養教育科目）必修": 1 ,
}

print("---------------------------------------")
print("division \nyour_score / graduation_requirements")
print("---------------------------------------\n")
for div in graduation_requirements:
    if earned_division_credit[div] >= graduation_requirements[div]:
        print(div,"\n", earned_division_credit[div],"/",
                graduation_requirements[div],"...OK")
    else:
        print(div,"\n", earned_division_credit[div],"/",
                graduation_requirements[div]," Doesn't meet this requirement!!!")

print("\n--------------- summary ----------------")
print("sum of your all scores", sum(earned_division_credit.values()))
print("sum of grad scores", sum(graduation_requirements.values()))
print("Note: Not matched if there are credits above graduation requirements")

