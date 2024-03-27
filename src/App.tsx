import React, {useEffect, useState} from 'react';
import {
  Image,
  NativeModules,
  Platform,
  SafeAreaView,
  StyleSheet,
  TouchableOpacity,
} from 'react-native';
import Images from './assests/icons';

const App = () => {
  const {RNChangeIcon} = NativeModules;

  const [currentIconName, setCurrentIconName] = useState('');

  const iconNamesAndroid = {
    DEFAULT: 'Default',
    BLACK: 'Black',
    PREMIUM: 'Premium',
  };

  const iconNamesIOS = {
    DEFAULT: null,
    BLACK: 'AppIcon-Black',
    PREMIUM: 'AppIcon-Premium',
  };

  const iconNames = Platform.OS === 'ios' ? iconNamesIOS : iconNamesAndroid;

  async function getIcon() {
    const icon = await RNChangeIcon.getIcon();
    setCurrentIconName(icon);
    console.log(icon);
  }

  const handleSetDefaultIcon = () => {
    RNChangeIcon.changeIcon(iconNames.DEFAULT)
      .then(() => {
        getIcon();
      })
      .catch((e: {message: string}) => console.log(e.message));
  };

  const handleSetDarkIcon = () => {
    RNChangeIcon.changeIcon(iconNames.BLACK)
      .then(() => {
        getIcon();
      })
      .catch((e: {message: string}) => console.log(e.message));
  };

  const handleSetLFIcon = () => {
    RNChangeIcon.changeIcon(iconNames.PREMIUM)
      .then(() => {
        getIcon();
      })
      .catch((e: {message: string}) => console.log(e.message));
  };

  useEffect(() => {
    getIcon();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentIconName]);

  return (
    <SafeAreaView style={styles.container}>
      <TouchableOpacity
        style={styles.linkButton}
        onPress={handleSetDefaultIcon}>
        <Image source={Images.icon_light} style={styles.iconImage} />
        {currentIconName === iconNames.DEFAULT ||
        currentIconName === 'AppIcon' ? (
          <Image source={Images.icon_star} />
        ) : null}
      </TouchableOpacity>
      <TouchableOpacity style={styles.linkButton} onPress={handleSetDarkIcon}>
        <Image source={Images.icon_dark} style={styles.iconImage} />
        {currentIconName === iconNames.BLACK ? (
          <Image source={Images.icon_star} />
        ) : null}
      </TouchableOpacity>
      <TouchableOpacity style={styles.linkButton} onPress={handleSetLFIcon}>
        <Image source={Images.icon_lf} style={styles.iconImage} />
        {currentIconName === iconNames.PREMIUM ? (
          <Image source={Images.icon_star} />
        ) : null}
      </TouchableOpacity>
    </SafeAreaView>
  );
};

export default App;

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  linkButton: {
    width: 100,
    aspectRatio: 1,
  },
  iconImage: {
    width: '100%',
    height: '100%',
    resizeMode: 'cover',
  },
});
