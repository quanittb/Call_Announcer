./gradlew clean

rm output/*
rm app/build/outputs/bundle/appReleaseRelease/*

./gradlew assembleAppTestRelease
cp app/build/outputs/apk/appTest/release/* output/
##
./gradlew bundleAppReleaseRelease
cp app/build/outputs/bundle/appReleaseRelease/* output/
