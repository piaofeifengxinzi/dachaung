#!/bin/bash
# 进入用户根目录
cd ~
# 删除原先的项目
rm -rf dachaung
#检查并安装环境，目前是检查maven，git，jre
checkEvn(){
if ! type java >/dev/null 2>&1; then
    echo 'java 未安装';
    echo '开始安装default-jre';
    sudo apt install default-jre -y;
else
    echo 'jre 已安装';
fi

if ! type mvn >/dev/null 2>&1; then
    echo 'maven 未安装';
    echo '开始安装maven';
    sudo apt install maven -y;
else
    echo 'maven 已安装';
fi

if ! type git >/dev/null 2>&1; then
    echo 'git 未安装';
    echo '开始安装git';
    sudo apt install git -y;
else
    echo 'git 已安装';
fi
}
# 检查用户环境
checkEvn;
# 进入用户根目录
cd ~
# 检查项目是否存在，检查项目文件夹
# 不检查了，好像重新下载会覆盖项目

# 从git下载项目
git clone https://github.com/piaofeifengxinzi/dachaung.git
# 从这里进入项目跟文件夹
cd ./dachaung
# 使用maven构建项目，这可能会需要一点时间
mvn clean package
# 完成后进入target目录
cd target
# 在target中会有一个.jar文件，使用这个文件就可以直接开启服务了
java -jar *.jar
