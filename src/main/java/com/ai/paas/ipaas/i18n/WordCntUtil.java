package com.ai.paas.ipaas.i18n;

import com.ai.paas.ipaas.util.StringUtil;

public class WordCntUtil {
	private WordCntUtil() {
		// 禁止私有化
	}

	public static int count(String text) {
		if (StringUtil.isBlank(text))
			return 0;
		text = escape(text);
		// 好像对俄语不准啊
		String[] words = text.split(" +");
		int total = 0;
		for (int i = 0; i < words.length; i++) {
			total = total + wordCount(words[i]);
		}
		return total;
	}

	private static int wordCount(String word) {
		if (StringUtil.isBlank(word))
			return 0;
		word = word.trim();
		// 再去一遍换行符
		word = word.replaceAll("\\s", "");
		if (StringUtil.isBlank(word))
			return 0;
		int i = 0;
		int len = word.length();
		int count = 0;
		boolean before = false;
		for (i = 0; i < len; ++i) {
			// 此处算法为：1.如果是英文字母，则需要看一下字符，直到遇到一个其他语言字符，此时前面字数加1，如果到单词尾也加一
			// 中、日、韩分别区间，三者标点符号为相应区间，都算一个字
			// 英语、法语、俄语、葡萄牙语处理一样，所以这里就不判断了
			// 汉语
			char c = word.charAt(i);
			// 为了扩展每种语言单独处理
			// 中文
			if ((c >= 0x2e80 && c <= 0x2fdf) || (c >= 0x3100 && c <= 0x312f)
					|| (c >= 0x3400 && c <= 0x4dbf)
					|| (c >= 0x4e00 && c <= 0x9fa5)
					|| (c >= 0xf900 && c <= 0xfaff)
					|| (c >= 0x2600 && c <= 0x27bf)
					|| (c >= 0x2800 && c <= 0x28ff)) {
				if (before) {
					++count;
					before = false;
				}
				++count;
				// 日语
			} else if ((c >= 0x3040 && c <= 0x30ff)
					|| (c >= 0x31f0 && c <= 0x31ff)) {
				if (before) {
					++count;
					before = false;
				}
				++count;
				// 韩语
			} else if ((c >= 0x1100 && c <= 0x11ff)
					|| (c >= 0x3130 && c <= 0x318f)
					|| (c >= 0xac00 && c <= 0xd7af)) {
				if (before) {
					++count;
					before = false;
				}
				++count;
				// 其他中日韩补充，如符号、音标
			} else if ((c >= 0xff00 && c <= 0xffef)
					|| (c >= 0x3000 && c <= 0x303f)
					|| (c >= 0x31c0 && c <= 0x31ef)
					|| (c >= 0x2ff0 && c <= 0x2fff)
					|| (c >= 0x31a0 && c <= 0x31bf)
					|| (c >= 0x1d300 && c <= 0x1d35f)
					|| (c >= 0x4dc0 && c <= 0x4dff)
					|| (c >= 0xa000 && c <= 0xa48f)
					|| (c >= 0xa490 && c <= 0xa4cf)
					|| (c >= 0x3200 && c <= 0x33ff)
					|| (c >= 0xfe10 && c <= 0xfe1f)
					|| (c >= 0xfe30 && c <= 0xfe4f)) {
				if (before) {
					++count;
					before = false;
				}
				++count;

			} else if ((c >= 0x0000 && c <= 0x0020)
					|| (c >= 0x007f && c <= 0x009f)) {
				// 需要处理掉一些特殊符号，不算字
			} else {
				// 俄语、法语、西班牙语和英语相同，以空格分隔
				// 说明前面有字母
				before = true;
			}
			// alert(char+"----"+count);
		}
		// 最后判定一下
		if (before)
			++count;
		return count;
	}

	private static String escape(String text) {
		if (StringUtil.isBlank(text))
			return null;
		// 这里还得考虑将制表符等换成空格
		text = text.replaceAll("\\s", " ");
		// java区分非换行空格，需要特殊处理
		text = text.replaceAll("\\p{javaSpaceChar}", " ");
		text = text.replaceAll("\u00A0", " ");
		// 替换回车和换行为空格
		text = text.replaceAll("\\r\\n|\\r|\\n", " ");
		return text;
	}

	public static void main(String[] args) {
		String cnText = "首  页  增  加几项数据的显示语料、客户、译员、订单、语种，用来增加平台的吸引力，每天以一定的数值自动增加，增加规则如下所示。并且也可以通过后台对数值进行更改，以更改后的数值作为新的基数自动增加。";
		System.out.println("cn:" + count(cnText));
		String enText = "Home to add a few data to show the corpus, customers, translators, orders, languages, used to increase the attractiveness of the platform, a certain number of automatic increase every day, the rules are shown below. And you can change the value of the data in the background, to change the value as a new base automatically.";
		System.out.println("en:" + count(enText));
		String krText = "홈페이지 추가 몇 가지 데이터 디스플레이 재료, 고객, 통역, 주문, 언어, 쓸 플랫폼 가지는 매력 으로 증가, 매일 일정한 수치 자동 증가 증가 규칙은 다음과 같다 개 주시기 바랍니다.그리고 해도 통해 무대 수치 내각제에 대해 로 변경 후 수치 로서 새로운 기수 자동 증가했다.";
		System.out.println("kr:" + count(krText));
		String jpText = "トップページ増加何項データの表示資料や取引先、通訳、注文、言語、用いて増加プラットフォームの魅力を、毎日一定の数値自動増加、増加ルールは以下の通り。また、バックグラウンドの数値で変更、変更後の数値として新たな基数の自動増加とすることも可能です。";
		System.out.println("jp:" + count(jpText));
		String spText = "El aumento de los datos de la muestra de datos, varios clientes, el intérprete, el orden, la lengua, la plataforma para aumentar el atractivo de un valor numérico automáticamente cada día con el aumento de las normas que figuran a continuación.Y también por el valor de cambio, el valor numérico de la base después de los cambios como un nuevo aumento automático.";
		System.out.println("sp:" + count(spText));
		String ruText = " Домашняя страница  добавить  несколько  отображения данных  корпус  , клиентов  ,  переводчик  ,  заказы  , языки  ,  платформы  для  увеличения  привлекательности  ,  с определенной  численное  автоматически увеличивается  каждый день  ,  увеличение  правил  , как показано ниже  .  и  также может быть  за кулисами  на  численное  изменить,  чтобы изменить  численное  в качестве новой  базы  после  автоматического увеличения  . ";
		System.out.println("ru:" + count(ruText));
		String frText = "La page d'accueil de l'augmentation de corpus, des clients, des interprètes, de l'ordre d'affichage, langues, plusieurs données de plate - forme pour augmenter l'attrait, chaque jour, pour une certaine valeur augmente automatiquement, l'ajout de règles comme indiqué ci - dessous.Et également par l'arrière - plan de modification numérique à numérique après le changement comme nouvelle base augmente automatiquement.";
		System.out.println("fr:" + count(frText));
	}
}
