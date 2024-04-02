cd ..

teamcity_tests_directory=$PWD

workdir="teamcity_tests_infrastructure"
teamcity_server_workdir="teamcity_server"
teamcity_agent_workdir="teamcity_agent"
selenoid_workdir="selenoid"

teamcity_server_container_name="teamcity_server_instance"
teamcity_agent_container_name="teamcity_agent_instance"
selenoid_container_name="selenoid_instance"
selenoid_ui_container_name="selenoid_ui_instance"

workdir_names=("$teamcity_server_workdir" "$teamcity_agent_workdir" "$selenoid_workdir")
container_names=("$teamcity_server_container_name" "$teamcity_agent_container_name" "$selenoid_container_name" "$selenoid_ui_container_name")

################################ 1. Получить IP адрес локальной машины
echo "Request IP"
ip=$(ipconfig | findstr "IPv4-адрес" | findstr "[0-9]*\.[0-9]*\.[0-9]*\.[0-9]*" | head -n 1 | cut -d: -f2 | tr -d ' ')
echo "Current IP: $ip"

############################### 2. Удалить данные с прошлого запуска (директории и контейнеры)
echo "Delete previous run data"

for i in "${container_names[@]}"; do
  docker stop $i
  docker rm $i
done

rm -rf $workdir
mkdir $workdir
cd $workdir

for i in "${workdir_names[@]}"; do
  mkdir $i
done

############################### 3. Запуск teamcity сервера
echo "Start teamcity server"

cd $teamcity_server_workdir
mkdir logs
mkdir datadir
docker run -d --name $teamcity_teamcity_server_container_name -v C:/Users/1/Desktop/workshop/version2/teamcity-testing-framework/teamcity_tests_infrastructure/$teamcity_server_workdir/datadir -v C:/Users/1/Desktop/workshop/version2/teamcity-testing-framework/teamcity_tests_infrastructure/$teamcity_server_workdir/logs -p 8111:8111 jetbrains/teamcity-server
#docker run --name teamcity-server-instance -v $PWD/$teamcity_server_workdir/datadir:/data/teamcity_server/datadir -v $PWD/$teamcity_server_workdir/logs:/opt/teamcity/logs -p 8111:8111 jetbrains/teamcity-server


echo "Teamcity Server is running..."

############################### 4. Запуск Selenoid
echo "Start selenoid"

cd .. && cd $selenoid_workdir
mkdir config
cp $teamcity_tests_directory/infra/browsers.json config/

docker run -d --name $selenoid_container_name -p 4444:4444 -v /tmp/docker.sock:/var/run/docker.sock -v C:/Users/1/Desktop/workshop/version2/teamcity-testing-framework/teamcity_tests_infrastructure/$selenoid_workdir/config/:/etc/selenoid/:ro aerokube/selenoid:latest-release

image_names=($(awk -F'"' '/"image": "/{print $4}' "$PWD/config/browsers.json"))
echo "Pull all browser images: ${image_names[@]}"

for image in "${image_names[@]}"; do
  docker pull $image
done

############################### 5. Запуск Selenoid-ui
echo "Start selenoid-ui"
docker run -d --name $selenoid_ui_container_name -p 8080:8080 aerokube/selenoid-ui:latest-release --selenoid-uri "http://$ip:4444"

############################### 6. Мануальные шаги по установке TeamCity Server
echo "Setup teamcity server"
cd .. && cd ..
mvn clean test -Dtest=SetupTest#startUpTest

############################### 7. Парсим суперюзер токен
echo "Parse superuser token"
superuser_token=$(grep -o 'Super user authentication token: [0-9]*' $teamcity_tests_directory/infra/$workdir/$teamcity_server_workdir/logs/teamcity-server.log | awk '{print $NF}')
echo "Super user token: $superuser_token"

############################### 8. Запуск тестов
echo "Run system tests"
cd .. && cd .. && cd ..

echo -e "host=$ip:8111\nsuperUserToken=admin:admin\nremote=http://$ip:4444/wd/hub\nbrowser=firefox" > $teamcity_tests_directory/src/main/resources/config.properties
cat $teamcity_tests_directory/src/main/resources/config.properties

echo "Run API tests"
mvn test -DsuiteXmlFile=testng-suites/api-suite.xml

echo "Run UI tests"
mvn test -DsuiteXmlFile=testng-suites/ui-suite.xml