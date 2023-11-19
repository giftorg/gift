package com.day;
import java.util.ArrayList;
import java.util.List;

class Project {
    private String docContent;
    private String projectName;
    private List<String> tags;

    private String translation;

    public Project(String docContent, String projectName) {
        this.docContent = docContent;
        this.projectName = projectName;
        this.tags = new ArrayList<>();
    }

    public String getDocContent() {
        return docContent;
    }

    public void setDocContent(String docContent) {
        this.docContent = docContent;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

//    ArrayList<String> taggingToProject(String doc) {
//        // 在这里编写使用星火大模型分析文档内容，获取标签列表的代码逻辑
//        StarfireModel model = new StarfireModel();    // 假设已经实现了一个名为StarfireModel的类
//        ArrayList<String> tags = model.analyze(this.docContent);    //其中有一个analyze方法用于分析文档内容
//        return tags;
//    }
    public String translateToProject(String doc) throws Exception {
        // 在这里编写使用星火大模型分析文档内容，获取标签列表的代码逻辑
        BigModelNew model = new BigModelNew();// 假设已经实现了一个名为StarfireModel的类
        String tanslation = model.modelTranslate(this.docContent);    //其中有一个analyze方法用于分析文档内容
        return tanslation;
    }

    public ArrayList<String> taggingToProject(String translation) throws Exception {
        // 在这里编写使用星火大模型分析文档内容，获取标签列表的代码逻辑
        BigModelNew model = new BigModelNew();// new一个名为model的BigModelNew类
        ArrayList<String> tags = model.modelTags(this.getTranslation());    //其中有一个analyze方法用于分析文档内容
        return tags;
    }

//// 下面这个是打标签的测试代码：
//    public static void main(String[] args) throws Exception {
////        String t1 = "My teacher, Miss Wang, helped me a lot in my middle school life. She is a kind easygoing woman. I must thank her for making a confident girl.　　I used to be a shy and unconfident girl. Mrs. Wang noticed me. She took good care of me and encouraged me to join the school speech contest. Of course, I failed. But Mrs. Wang cheered me up and said every man is the architect of his own future.　　From then on, I practiced every day. It goes without saying “No pain, no gain.” I won the contest in the second term. In my opinion, teachers are the same as gardeners and they volunteer today and gain tomorrow. Not only can they teach knowledge but also they can teach students how to be a successful man.　　Thank you, Mrs. Wang. You make a duck become a beautiful swan. I want to be a teacher that as same as you in the future.";
//        String t1 = "老洋之家`项目是一个（个人博客系统），包括前台博客页面及后台管理系统，基于SpringBoot+MyBatis实现，采用Tomcat容器部署。前台博客页面包含：首页、博客文章页面、博客分类、归档页面、关于我页面、搜索页面、登录页面。 后台管理系统包含：博客管理、分类管理、标签管理、用户管理 ";
//        Project p = new Project(t1, "1");
//        p.setTranslation(p.translateToProject(t1));
//        System.out.println(p.taggingToProject(p.getTranslation()));
//    }


// 下面这个是翻译的测试代码：
    public static void main(String[] args) throws Exception {
         String t1 = "My teacher, Miss Wang, helped me a lot in my middle school life. She is a kind easygoing woman. I must thank her for making a confident girl.　　I used to be a shy and unconfident girl. Mrs. Wang noticed me. She took good care of me and encouraged me to join the school speech contest. Of course, I failed. But Mrs. Wang cheered me up and said every man is the architect of his own future.　　From then on, I practiced every day. It goes without saying “No pain, no gain.” I won the contest in the second term. In my opinion, teachers are the same as gardeners and they volunteer today and gain tomorrow. Not only can they teach knowledge but also they can teach students how to be a successful man.　　Thank you, Mrs. Wang. You make a duck become a beautiful swan. I want to be a teacher that as same as you in the future.";
       // String t1 = "老洋之家`项目是一个（个人博客系统），包括前台博客页面及后台管理系统，基于SpringBoot+MyBatis实现，采用Tomcat容器部署。前台博客页面包含：首页、博客文章页面、博客分类、归档页面、关于我页面、搜索页面、登录页面。 后台管理系统包含：博客管理、分类管理、标签管理、用户管理 ";
        Project p = new Project(t1, "1");
        System.out.print(p.translateToProject(t1));

    }

}