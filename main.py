import requests  # 导入模块requests
import csv  #csv文件
import time
from PaChong.crawler_github.domain.Project import Project   #导入实体类
from PaChong.crawler_github.Enum.LanguageEnum import Languages

'''
这个文件是用来返回某种指定语言的项目仓库信息
https://api.github.com/search/repositories?q=language:python&sort=stars&page=4&per_page=100 每页100 第四页
思路，for循环遍历100×100，实现遍历100页
!!!需要定时更新
加入封装对象
'''
countpages = 1  #统计页数
# 指定 CSV 文件路径
csv_file_path = 'projects.csv'

def find100GitHub(language,pageId):
    # 执行API调用并存储响应

    url = 'https://api.github.com/search/repositories?q=language:{}&sort=stars&page={}&per_page=100'.format(language,pageId)
    r = requests.get(url)  # requests来执行调用

    # 将API响应存储在一个变量中
    response_dict = r.json()  # 使用json()将这些信息转换为Python字典

    # 探索有关仓库信息
    if 'items' in response_dict:
        repo_dicts = response_dict['items']
    else:
        print(language,"语言目前的页数:",pageId)
        return "没有项目仓库了"
    # 处理没有'items'键的情况
    # 可以抛出异常、返回默认值或进行其他处理

    itemlist_length = len(repo_dicts)
    if itemlist_length == 0 :
        print("Repositories returned：", len(repo_dicts))
        return
    print("\nSelected information about first repository：\n")
    count = 0
    for repo_dict in repo_dicts:
        count += 1
        print('项目', count)
        itemName = repo_dict['name']
        Stars = repo_dict['watchers_count']
        loginName = repo_dict['owner']['login']
        Repository = repo_dict['html_url']
        Description = repo_dict['description'] #可能有Uniode字符，转换成utf-8
        project = Project(itemName=itemName, Stars=Stars, loginName=loginName,
                Repository=Repository, Description=Description)
        print(project.to_list())
        proJect_list.append(project)
        print('\n')

# 将实例保存到 CSV 文件
def save_to_csv(file_path, projects):
    with open(file_path, mode='w', newline='',encoding='utf-8') as file:
        writer = csv.writer(file)
        # 写入 CSV 文件的表头
        writer.writerow(Project.header())
        # 写入每个实例的数据
        for project in projects:
            writer.writerow(project.to_list())


proJect_list=[]

flag = 0
for language in Languages:      #切换语言
    print('++++++++++++%s++++++++++++' % language.value)
    if flag == 0 :
        countpages = 1  # 换语言页数时清零
    flag = 1
    for i in range (1,11):    #每个语言的列表 十个页面 一页100个
        print('======================目前%s语言的第%d页=====================' % (language.value,i))
        find100GitHub(language.value,i)
        countpages += 1
    time.sleep(30)
print("总页数为：%d页" % (countpages - 1))
itemcounts = (countpages - 1) * 100
print("项目总数为%d" % itemcounts)
# 将实例列表保存到 CSV 文件
save_to_csv(csv_file_path, proJect_list)
