from os import listdir
from os.path import isfile, join
import numpy as np
import matplotlib.pyplot as plt
import pylab

colors = ["#E66908", "#FFCE6B", "#5F7EC6", "#FF8A6B", "#B3F064", "#8D5DC7", "#FFF36B", "#0000FF", "#0000FF"]

agent_names = ["DoNothing", "Random", "SIMPLE", "OSLA", "MC", "MCTS", "RHEA", "OEP", "PORTFOLIO_MCTS"]
agents_dict = {"DoNothing" : "DoNothing", "RandomAgent" : "Random", "SimpleAgent" : "SIMPLE", "OneStepLookAheadAgent" : "OSLA",
               "MonteCarloAgent" : "MC", "MCTSPlayer":"MCTS", "RHEAAgent":"RHEA", "OEP":"OEP", "PortfolioMCTSPlayer" : "PORTFOLIO_MCTS"}

game_modes = ["CAPITALS", "SCORE"]


N_GAMES = 25
REP = 20
NUM_TECHS = 24

def process_file(f_name):

    nparams = len(f_name.split(".")[-2].split("_"))
    agents = []
    victories = {}
    positions = {}
    points_l = {}
    techs = {}
    cities_l = {}
    prods = {}
    gpstats = {}
    # for ag in agents:
    #     victories[ag] = []
    #     positions[ag] = []
    #     points_l[ag] = []
    #     techs[ag] = []
    #     cities_l[ag] = []
    #     prods[ag] = []

    with open(f_name) as f:

        lines = f.readlines()
        line2 = lines[1]

        agents.append(line2.split(":")[1].split("(")[0])
        agents.append(line2.split(":")[2].split("(")[0])

    seed_per_game = [-1 for _ in range(N_GAMES)]
    data_per_game = {}
    all_data = {}
    all_features = {}
    all_features_per_game = {}
    sum_data = {}
    for ag in agents:
        data_per_game[ag] = [[] for _ in range(N_GAMES)]
        all_data[ag] = [[] for _ in range(6)]
        sum_data[ag] = []
        all_features[ag] = {}
        all_features_per_game[ag] = [[] for _ in range(N_GAMES)]

    with open(f_name) as f:
        lines = f.readlines()
        rep = 0
        game = -1

        for line in lines:
            if "Playing level with seed" in line or "RESULTS" in line:

                if "Playing level with seed" in line:
                    seed = line.split(" ")[-2]
                    seed_per_game[game+1] = seed
                    # print "game " + str(game) + " seed " + str(seed)

                if rep > 0:
                    for ag in agents:
                        game_info = []
                        game_info.append(np.average(victories[ag]))
                        game_info.append(np.average(positions[ag]))
                        game_info.append(np.average(points_l[ag]))
                        game_info.append(np.average(techs[ag]))
                        game_info.append(np.average(cities_l[ag]))
                        game_info.append(np.average(prods[ag]))
                        data_per_game[ag][game] = game_info

                        for featname in gpstats[ag]:
                            if featname not in all_features[ag]:
                                all_features[ag][featname] = []
                            for v in gpstats[ag][featname]:
                                all_features[ag][featname].append(v)
                        all_features_per_game[ag][game].append(gpstats[ag])


                rep = 0
                game = game + 1
                victories = {}
                positions = {}
                points_l = {}
                techs = {}
                cities_l = {}
                prods = {}
                for ag in agents:
                    victories[ag] = []
                    positions[ag] = []
                    points_l[ag] = []
                    techs[ag] = []
                    cities_l[ag] = []
                    prods[ag] = []
                    gpstats[ag] = {}



            if "Playing with" in line:
                rep = rep + 1
                # print "game " + str(game) + " seed " + str(seed) + " rep " + str(rep)


            if "#1" in line or "#2" in line:
                # results
                agent_chunk = line.split(";")[0].split(" ")[4]
                agent = agents_dict[agent_chunk[1:-2]]

                v = 1 if "#1" in line else 0
                p = 1 if "#1" in line else 2
                pt = int(line.split(";")[0].split(" ")[-2])
                t = int(line.split(";")[1].split(",")[0].split(" ")[-1])
                c = int(line.split(";")[1].split(",")[1].split(" ")[-1])
                pr = int(line.split(";")[1].split(",")[2].split(" ")[-1])

                victories[agent].append(v)
                positions[agent].append(p)
                points_l[agent].append(pt)
                techs[agent].append(t)
                cities_l[agent].append(c)
                prods[agent].append(pr)

                all_data[agent][0].append(v)
                all_data[agent][1].append(p)
                all_data[agent][2].append(pt)
                all_data[agent][3].append(t)
                all_data[agent][4].append(c)
                all_data[agent][5].append(pr)


            if "GPS:" in line:
                chunks = line.split(":")
                agent = agents_dict[chunks[2]]
                feature = chunks[3]
                value = float(chunks[4])

                if feature not in gpstats[agent]:
                    gpstats[agent][feature] = []
                gpstats[agent][feature].append(value)


    for ag in agents:
        agent_data = all_data[ag]
        n = len(agent_data[0])
        sum_data[ag].append(n)
        v_avg = np.average(agent_data[0])
        v_stderr = np.std(agent_data[0])/np.sqrt(n)
        sum_data[ag].append(v_avg)
        sum_data[ag].append(v_stderr)
        p_avg = np.average(agent_data[1])
        p_stderr = np.std(agent_data[1])/np.sqrt(n)
        sum_data[ag].append(p_avg)
        sum_data[ag].append(p_stderr)
        pt_avg = np.average(agent_data[2])
        pt_stderr = np.std(agent_data[2])/np.sqrt(n)
        sum_data[ag].append(pt_avg)
        sum_data[ag].append(pt_stderr)
        t_avg = np.average(agent_data[3])
        t_stderr = np.std(agent_data[3])/np.sqrt(n)
        sum_data[ag].append(t_avg)
        sum_data[ag].append(t_stderr)
        c_avg = np.average(agent_data[4])
        c_stderr = np.std(agent_data[4])/np.sqrt(n)
        sum_data[ag].append(c_avg)
        sum_data[ag].append(c_stderr)
        pr_avg = np.average(agent_data[5])
        pr_stderr = np.std(agent_data[5])/np.sqrt(n)
        sum_data[ag].append(pr_avg)
        sum_data[ag].append(pr_stderr)


    return agents, all_data, sum_data, data_per_game, seed_per_game, all_features, all_features_per_game


def print_agent_pw(ag, data, column_order):
    # print data

    all_txt = ag
    for key in column_order:
        if key == ag:
            txt = " & *"
        else:
            d = data[ag][key]
            txt = " & {a:.2f}\% ({b:.2f})".format(a=d[0], b=d[1])
        all_txt = all_txt + txt
          #+ " & {a:.2f}\% ({b:.2f}) & {c:.2f} ({d:.2f}) & {e:.2f} ({f:.2f}) & {g:.2f}\% ({h:.2f}) & {i:.2f} ({j:.2f}) & {k:.2f} ({l:.2f}) \\\\"

    all_txt = all_txt + "\\\\"
    all_txt = all_txt + "\\hline"
    print (all_txt)


    #
    # print(txt.format(
    #     a=data[ag]['tot'][0], b=victories[ag]['tot'][1],
    #     c=positions[ag]['tot'][0], d=positions[ag]['tot'][1],
    #     e=scores[ag]['tot'][0], f=scores[ag]['tot'][1],
    #     g=technologies[ag]['tot'][0], h=technologies[ag]['tot'][1],
    #     i=cities[ag]['tot'][0], j=cities[ag]['tot'][1],
    #     k=productions[ag]['tot'][0], l=productions[ag]['tot'][1]))

def main():

    file_path = "C:\\Work\\Tribes-results\\validation-5-6-6\\"
    column_order = ["MCTS", "PORTFOLIO_MCTS"]
    files = [join(file_path, f) for f in listdir(file_path) if isfile(join(file_path, f)) and f.endswith("test.txt")]

    victories = {}
    positions = {}
    scores = {}
    points = {}
    technologies = {}
    cities = {}
    productions = {}
    all_features = {}
    all_features_per_game = {}

    for ag in agent_names:
        victories[ag] = {}
        positions[ag] = {}
        scores[ag] = {}
        technologies[ag] = {}
        cities[ag] = {}
        productions[ag] = {}
        all_features[ag] = {}
        all_features_per_game[ag] = {}

        victories[ag]['tot'] = []
        positions[ag]['tot'] = []
        scores[ag]['tot'] = []
        technologies[ag]['tot'] = []
        cities[ag]['tot'] = []
        productions[ag]['tot'] = []


    for f in files:

        if "tribes_" in f:

            print (" --- " + f + " --- ")

            # game_mode = game_modes[int(f.split(".")[-2].split("_")[1])]
            # length = int(f.split(".")[-2].split("_")[3])
            # pop_size = int(f.split(".")[-2].split("_")[6])

            agents, all_data, sum_data, data_per_game, seed_per_game, all_features, all_features_per_game = process_file(f)

            victories[agents[0]][agents[1]] = [sum_data[agents[0]][1] * 100, sum_data[agents[0]][2] * 100]
            victories[agents[1]][agents[0]] = [sum_data[agents[1]][1] * 100, sum_data[agents[1]][2] * 100]
            victories[agents[0]]['tot'].extend(all_data[agents[0]][0])
            victories[agents[1]]['tot'].extend(all_data[agents[1]][0])

            positions[agents[0]][agents[1]] = str(sum_data[agents[0]][3]) + " (" + str(sum_data[agents[0]][4]) + ")"
            positions[agents[1]][agents[0]] = str(sum_data[agents[1]][3]) + " (" + str(sum_data[agents[1]][4]) + ")"
            positions[agents[0]]['tot'].extend(all_data[agents[0]][1])
            positions[agents[1]]['tot'].extend(all_data[agents[1]][1])

            scores[agents[0]][agents[1]] = str(sum_data[agents[0]][5]) + " (" + str(sum_data[agents[0]][6]) + ")"
            scores[agents[1]][agents[0]] = str(sum_data[agents[1]][5]) + " (" + str(sum_data[agents[1]][6]) + ")"
            scores[agents[0]]['tot'].extend(all_data[agents[0]][2])
            scores[agents[1]]['tot'].extend(all_data[agents[1]][2])

            technologies[agents[0]][agents[1]] = str(sum_data[agents[0]][7] * 100 / 24) + " (" + str(sum_data[agents[0]][8]) + ")"
            technologies[agents[1]][agents[0]] = str(sum_data[agents[1]][7] * 100 / 24) + " (" + str(sum_data[agents[1]][8]) + ")"
            technologies[agents[0]]['tot'].extend(all_data[agents[0]][3])
            technologies[agents[1]]['tot'].extend(all_data[agents[1]][3])


            cities[agents[0]][agents[1]] = str(sum_data[agents[0]][9]) + " (" + str(sum_data[agents[0]][10]) + ")"
            cities[agents[1]][agents[0]] = str(sum_data[agents[1]][9]) + " (" + str(sum_data[agents[1]][10]) + ")"
            cities[agents[0]]['tot'].extend(all_data[agents[0]][4])
            cities[agents[1]]['tot'].extend(all_data[agents[1]][4])

            productions[agents[0]][agents[1]] = str(sum_data[agents[0]][11]) + " (" + str(sum_data[agents[0]][12]) + ")"
            productions[agents[1]][agents[0]] = str(sum_data[agents[1]][11]) + " (" + str(sum_data[agents[1]][12]) + ")"
            productions[agents[0]]['tot'].extend(all_data[agents[0]][5])
            productions[agents[1]]['tot'].extend(all_data[agents[1]][5])


            # print agents
            # print all_data
            # print sum_data
            # print data_per_game

                # plot(all_scores_n, False, 'upper right')

    # column_order = ["RHEA", "RuleBased", "MCTS", "MC", "OSLA", "Random"]
#    column_order = ["RHEA", "RuleBased", "MCTS", "MC", "OSLA"]
#     column_order = ["MCTS", "PORTFOLIO_MCTS"]
#     column_order = ["SIMPLE", "PORTFOLIO_MCTS"]
    # column_order = ["RHEA", "PORTFOLIO_MCTS"]
    # print_agent_pw("RHEA", victories, column_order)
    # print_agent_pw("SIMPLE", victories, column_order)
    # print_agent_pw("MCTS", victories, column_order)
    # print_agent_pw("PORTFOLIO_MCTS", victories, column_order)
    # print_agent_pw("MC", victories, column_order)
    # print_agent_pw("OSLA", victories, column_order)
    # print_agent_pw("Random", victories, column_order)

    if False:
        N_REPRESENTATIVES = 10
        representative = {} # [-1 for _ in column_order]
        for c in column_order:
            print_agent_pw(c, victories, column_order)
            representative[c] = []

        for agent in data_per_game:
            data = data_per_game[agent]
            avg_vict = np.average(victories[agent]['tot'])
            # for res in data:  # For each game
            for game_idx in range(len(data)):  # For each game
                res = data[game_idx]
                vict_err = np.power(res[0] - avg_vict, 2)
                representative[agent].append([game_idx, vict_err, avg_vict, res[0]])

            representative[agent].sort(key = lambda x: x[1])
            print("Most representative games for " + agent + " (avg win: " + str(avg_vict) + "): ")
            for i in range(0, N_REPRESENTATIVES):
                game_idx = representative[agent][i][0]
                print(str(i)+ ": game " + str(game_idx) + ", avg: " + str(representative[agent][i][3]) + ", err: " + str(representative[agent][i][1]) + ", seed: " + str(seed_per_game[game_idx]))

    print ("_____________")


    if True:
        for ag in agent_names:
            if len(victories[ag]['tot']) > 0:
                victories[ag]['tot'] = [np.average(victories[ag]['tot']) * 100, np.average(victories[ag]['tot']) / np.sqrt(len(victories[ag]['tot'])) * 100]
                positions[ag]['tot'] = [np.average(positions[ag]['tot']), np.average(positions[ag]['tot']) / np.sqrt(len(positions[ag]['tot']))]
                scores[ag]['tot'] = [np.average(scores[ag]['tot']), np.average(scores[ag]['tot']) / np.sqrt(len(scores[ag]['tot']))]
                technologies[ag]['tot'] = [np.average(technologies[ag]['tot']) * 100 / 24, np.average(technologies[ag]['tot']) / np.sqrt(len(technologies[ag]['tot'])) * 100 / 24]
                cities[ag]['tot'] = [np.average(cities[ag]['tot']), np.average(cities[ag]['tot']) / np.sqrt(len(cities[ag]['tot']))]
                productions[ag]['tot'] = [np.average(productions[ag]['tot']), np.average(productions[ag]['tot']) / np.sqrt(len(productions[ag]['tot']))]

                # LATEX
                txt = ag + " & {a:.2f}\% ({b:.2f}) & {c:.2f} ({d:.2f}) & {e:.2f} ({f:.2f}) & {g:.2f}\% ({h:.2f}) & {i:.2f} ({j:.2f}) & {k:.2f} ({l:.2f}) \\\\"
                print(txt.format(
                      a=victories[ag]['tot'][0], b=victories[ag]['tot'][1],
                      c=positions[ag]['tot'][0], d=positions[ag]['tot'][1],
                      e=scores[ag]['tot'][0], f=scores[ag]['tot'][1],
                      g=technologies[ag]['tot'][0], h=technologies[ag]['tot'][1],
                      i=cities[ag]['tot'][0], j=cities[ag]['tot'][1],
                      k=productions[ag]['tot'][0], l=productions[ag]['tot'][1]))
                print ("\\hline")

                if ag in all_features:
                    # txt_feat = ag
                    # txt_val = ag
                    human_readable = {}

                    for featname in all_features[ag]:
                        # txt_feat = txt_feat + " & " + featname
                        avg = np.average(all_features[ag][featname])
                        tv = " & {a:.2f}".format(a=avg)
                        # txt_val = txt_val + tv

                        human_readable[featname] = avg

                    # print(txt_feat)
                    # print(txt_val)

                    if True:
                        print(ag)
                        for f in human_readable:
                            print(f + ": " + str(human_readable[f]))


    # print victories

main()