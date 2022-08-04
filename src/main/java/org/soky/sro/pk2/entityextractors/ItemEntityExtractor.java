package org.soky.sro.pk2.entityextractors;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.sokybot.domain.Gender;
import org.sokybot.domain.Race;
import org.sokybot.domain.items.ItemEntity;
import org.sokybot.domain.items.ItemType;
import org.sokybot.pk2.IPk2File;
import org.sokybot.pk2.JMXFile;
import static org.sokybot.pk2.io.Pk2IO.getInputStream;


public class ItemEntityExtractor implements IPK2EntityExtractor<ItemEntity> {

    //private IPk2File pk2File;


    @Override
    public Stream<ItemEntity> extract(IPk2File pk2File) {

        return pk2File.find("itemdata.txt", 1)
                .stream()
                .findFirst()
                .map((itemDataFile) -> Stream.of(extractItemFileNames(itemDataFile)))
                .get()
                .flatMap((itemFileName) -> pk2File.find("(?i)" + itemFileName).stream())
                .flatMap(this::extractItems);


    }

    private String[] extractItemFileNames(JMXFile itemDataFile) {

        byte[] fileBytes = itemDataFile.getFileBytes();
        String fileStr = new String(fileBytes, Charset.forName("UTF-16"));
        return fileStr.split(System.getProperty("line.separator"));

    }


    private Stream<ItemEntity> extractItems(JMXFile file) {


        Set<ItemEntity> res = new HashSet<>();

        try {
            BufferedReader inReader = new BufferedReader(new InputStreamReader(getInputStream(file), "UTF-16"));
            Iterable<CSVRecord> records = CSVFormat.MYSQL.parse(inReader);

            for (CSVRecord record : records) {

                if (record.size() < 1 || record.size() < 58)
                    continue;
                if (record.get(0).startsWith("//"))
                    continue;

                ItemEntity entity = new ItemEntity();
                String field = record.get(1); // id

                if (NumberUtils.isParsable(field)) {
                    entity.setId(Integer.parseInt(field));
                } else
                    continue;

                field = record.get(2); // long id

                if (!field.isBlank()) {
                    entity.setLongId(field);
                } else
                    continue;

                field = record.get(5); // name
                entity.setName(field);

                field = record.get(7); // isMallItem {0 , 1}
                boolean isMall = false;
                if (NumberUtils.isParsable(field)) {
                    isMall = BooleanUtils.toBoolean(Byte.valueOf(field));
                }
                entity.setMallItem(isMall);

                String type = "";

                field = record.get(10); // type byte 1

                if (NumberUtils.isParsable(field)) {
                    type += Integer.toHexString(Integer.parseInt(field));
                } else
                    continue;

                field = record.get(11); // type byte 2

                if (NumberUtils.isParsable(field)) {
                    type += Integer.toHexString(Integer.parseInt(field));
                } else
                    continue;

                field = record.get(12); // type byte 3

                if (NumberUtils.isParsable(field)) {
                    type += Integer.toHexString(Integer.parseInt(field));
                } else
                    continue;

                entity.setItemType(ItemType.parseType(Integer.valueOf(type, 16), record.get(2)));

                field = record.get(14); // item race
                Race itemRace = Race.Universal;

                if (NumberUtils.isParsable(field)) {
                    itemRace = Race.parseType(Byte.valueOf(field));
                }
                entity.setRace(itemRace);

                field = record.get(15); // isSOX
                entity.setSOX(field.equals("1"));

                field = record.get(19);
                entity.setSortable(!field.equals("0"));

                field = record.get(33); // lvl
                int lvl = 0;
                if (NumberUtils.isParsable(field)) {
                    lvl = Integer.parseInt(field);
                }
                entity.setLevel(lvl);

                field = record.get(57);
                int maxStacks = 0;
                if (NumberUtils.isParsable(field)) {
                    maxStacks = Integer.parseInt(field);
                }
                entity.setMaxStacks(maxStacks);

                Gender itemGender = Gender.Unisex;

                if (record.size() > 58) {
                    field = record.get(58);
                    if (NumberUtils.isParsable(field)) {
                        itemGender = Gender.parseType(Byte.valueOf(field));
                    }
                }

                entity.setGender(itemGender);

                int degree = 0;

                if (record.size() > 61) {
                    field = record.get(61);
                    if (NumberUtils.isParsable(field)) {
                        degree = (Integer.parseInt(field) + 3 - 1) / 3;
                    }
                }
//					options.add(String.valueOf(degree));

                entity.setDegree(degree);

                res.add(entity);
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return res.stream();
    }


}
