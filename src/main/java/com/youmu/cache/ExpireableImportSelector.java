package com.youmu.cache;

import com.youmu.cache.annotation.EnableExpireableCache;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AdviceModeImportSelector;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2018/05/08
 */
public class ExpireableImportSelector extends AdviceModeImportSelector<EnableExpireableCache> {
	@Override
	protected String[] selectImports(AdviceMode adviceMode) {
		return new String[]{ExpireableConfig.class.getName()};
	}
}
