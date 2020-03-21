package com.wiblog.core.utils;

import org.ansj.app.keyword.KeyWordComputer;
import org.ansj.app.keyword.Keyword;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.recognition.impl.StopRecognition;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO 描述
 *
 * @author pwm
 * @date 2019/7/23
 */
@Component
public class WordFilterUtil {

    private StopRecognition filter;

    public WordFilterUtil(){
        filter = new StopRecognition();
        filter.insertStopNatures("w");
    }

    /**
     * 分词 去除停用词
     * @param text text
     * @return List
     */
    public List<String> getParticiple(String text){
        List<String> result = new ArrayList<>();
        List<Term> list = ToAnalysis.parse(text).recognition(filter).getTerms();
        for (Term item:list) {
            if (StringUtils.isNotBlank(item.getName())){
                result.add(item.getName());
                System.out.println(item.getName());
            }
        }
        return result;
    }
}
