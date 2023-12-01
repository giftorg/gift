#GitHub 仓库项目实体类

class Project:
    def __init__(self, itemName,Stars,loginName,Repository,Description,):
        self.itemName = itemName        #项目名称
        self.Stars = Stars              #收藏数量
        self.loginName = loginName      #登录名
        # self.starsGrade = starsGrade    #
        self.Repository = Repository    #项目仓库网址
        self.Description = Description   #项目仓库描述

    def to_list(self):
        # 将实例的属性转换为列表
        return [self.itemName, self.Stars, self.loginName,  self.Repository, self.Description]

    @staticmethod
    def header():
        # 返回 CSV 文件的表头
        return ['ItemName', 'Stars', 'LoginName', 'Repository', 'Description']

# 如果希望这个文件被其他模块导入时，不会自动执行下面的代码，可以使用如下形式：
# if __name__ == "__main__":
#     pass
